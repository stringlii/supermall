package com.litianyi.supermall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.common.constant.WareConstant;
import com.litianyi.supermall.ware.entity.PurchaseDetailEntity;
import com.litianyi.supermall.ware.service.PurchaseDetailService;
import com.litianyi.supermall.ware.service.WareSkuService;
import com.litianyi.supermall.ware.vo.MergeVo;
import com.litianyi.supermall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.ware.dao.PurchaseDao;
import com.litianyi.supermall.ware.entity.PurchaseEntity;
import com.litianyi.supermall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService detailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceived(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params),
                new LambdaQueryWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getStatus, 0)
                        .or().eq(PurchaseEntity::getStatus, 1));
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) throws Exception {
        Long purchaseId = mergeVo.getPurchaseId();
        PurchaseEntity purchaseEntity;
        if (purchaseId == null) {
            purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        } else {
            purchaseEntity = this.getById(purchaseId);
            if (purchaseEntity.getStatus() != WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    && purchaseEntity.getStatus() != WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                throw new Exception("采购单的状态必须是新建或已分配");
            }
        }

        List<Long> items = mergeVo.getItems();
        if (!CollectionUtils.isEmpty(items)) {
            List<PurchaseDetailEntity> detailEntityList = new ArrayList<>();
            for (Long item : items) {
                PurchaseDetailEntity detailEntity = detailService.getById(item);
                if (detailEntity.getStatus() != WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                        && detailEntity.getStatus() != WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                    throw new Exception("采购项的状态必须是新建或已分配");
                }

                detailEntity.setPurchaseId(purchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                detailEntityList.add(detailEntity);
            }

            //仓库id
            Long wareId = detailEntityList.get(0).getWareId();
            BigDecimal amount = new BigDecimal("0");

            for (PurchaseDetailEntity detailEntity : detailEntityList) {
                //排除不同仓库的采购需求
                if (!Objects.equals(detailEntity.getWareId(), wareId)) {
                    detailEntityList.remove(detailEntity);
                } else {
                    //计算总金额
                    BigDecimal skuPrice = Optional.ofNullable(detailEntity.getSkuPrice()).orElse(new BigDecimal("0"));
                    amount = amount.add(skuPrice);
                }
            }
            detailService.updateBatchById(detailEntityList);

            purchaseEntity.setWareId(wareId);
            purchaseEntity.setAmount(amount);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    @Override
    @Transactional
    public void received(List<Long> ids) {
        //采购单是新建或已分配状态
        List<PurchaseEntity> purchaseEntityList = ids.stream()
                .map(this::getById)
                .filter(entity -> entity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                        || entity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(entity -> {
                    entity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
                    entity.setUpdateTime(new Date());
                }).collect(Collectors.toList());
        //改变采购单的状态
        this.updateBatchById(purchaseEntityList);

        //改变采购需求的状态
        purchaseEntityList.forEach(item -> {
            List<PurchaseDetailEntity> detailEntityList = detailService.listByPurchaseId(item.getId()).stream()
                    .map(entity -> {
                        PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                        detailEntity.setId(entity.getId());
                        detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASING.getCode());
                        return detailEntity;
                    }).collect(Collectors.toList());
            detailService.updateBatchById(detailEntityList);
        });
    }

    @Override
    @Transactional
    public void purchaseDone(PurchaseDoneVo vo) {
        //1. 改变采购项的状态
        boolean flag = true;
        List<PurchaseDoneVo.PurchaseItemDoneVo> items = vo.getItems();
        List<PurchaseDetailEntity> updateItems = new ArrayList<>();
        for (PurchaseDoneVo.PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getItemId());
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode()) {
                flag = false;
            } else {
                //3. 入库
                PurchaseDetailEntity entity = detailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            detailEntity.setStatus(item.getStatus());
            detailEntity.setDescription(item.getReason());
            updateItems.add(detailEntity);
        }
        detailService.updateBatchById(updateItems);

        //2. 改变采购单的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(vo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISHED.getCode()
                : WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}