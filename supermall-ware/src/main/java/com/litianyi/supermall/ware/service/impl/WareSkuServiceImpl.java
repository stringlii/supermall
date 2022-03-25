package com.litianyi.supermall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.common.to.SkuInfoTo;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.ware.feign.ProductFeignService;
import com.litianyi.supermall.ware.vo.SkuHasStockVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.ware.dao.WareSkuDao;
import com.litianyi.supermall.ware.entity.WareSkuEntity;
import com.litianyi.supermall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq(WareSkuEntity::getSkuId, skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuEntity wareSkuEntity = this.getOne(new LambdaQueryWrapper<WareSkuEntity>()
                .eq(WareSkuEntity::getSkuId, skuId)
                .eq(WareSkuEntity::getWareId, wareId));
        if (wareSkuEntity != null) {
            baseMapper.updateStock(skuId, wareId, skuNum);
        } else {
            wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            R r = productFeignService.info(skuId);
            if (r.getCode() == 0) {
                SkuInfoTo skuInfo = r.getData("skuInfo", SkuInfoTo.class);
                wareSkuEntity.setSkuName(skuInfo.getSkuName());
            }
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            this.save(wareSkuEntity);
        }

        productFeignService.updateSkuStock(skuId, wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0);
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        return skuIds.stream().map(sku -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            vo.setSkuId(sku);

            List<WareSkuEntity> wareSkuEntities = this.list(new LambdaQueryWrapper<WareSkuEntity>()
                    .select(WareSkuEntity::getStock, WareSkuEntity::getStockLocked)
                    .eq(WareSkuEntity::getSkuId, sku));
            int total = 0;
            for (WareSkuEntity wareSkuEntity : wareSkuEntities) {
                Integer stock = wareSkuEntity.getStock();
                Integer stockLocked = wareSkuEntity.getStockLocked();
                if (stock == null) {
                    stock = 0;
                }
                if (stockLocked == null) {
                    stockLocked = 0;
                }
                total += (stock - stockLocked);
            }
            vo.setHasStock(total > 0);

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateAndEsById(WareSkuEntity wareSku) {
        this.updateById(wareSku);
        productFeignService.updateSkuStock(wareSku.getSkuId(), wareSku.getStock() - wareSku.getStockLocked() > 0);
    }

}