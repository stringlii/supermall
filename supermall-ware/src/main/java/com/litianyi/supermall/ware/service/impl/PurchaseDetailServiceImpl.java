package com.litianyi.supermall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.ware.entity.WareInfoEntity;
import com.litianyi.supermall.ware.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.ware.dao.PurchaseDetailDao;
import com.litianyi.supermall.ware.entity.PurchaseDetailEntity;
import com.litianyi.supermall.ware.service.PurchaseDetailService;

@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> w
                    .eq(PurchaseDetailEntity::getId, key)
                    .or().eq(PurchaseDetailEntity::getSkuId, key)
                    .or().eq(PurchaseDetailEntity::getWareId, key));
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listByPurchaseId(Long purchaseId) {
        return this.list(new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, purchaseId));
    }

}