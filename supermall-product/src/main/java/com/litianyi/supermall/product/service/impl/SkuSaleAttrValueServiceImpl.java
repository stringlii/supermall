package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.SkuInfoEntity;
import com.litianyi.supermall.product.service.SkuInfoService;
import com.litianyi.supermall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.SkuSaleAttrValueDao;
import com.litianyi.supermall.product.entity.SkuSaleAttrValueEntity;
import com.litianyi.supermall.product.service.SkuSaleAttrValueService;
import org.springframework.util.CollectionUtils;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.ItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        List<SkuItemVo.ItemSaleAttrVo> vos = new ArrayList<>();

        List<Long> skuIds = skuInfoService.listBySpuId(spuId).stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(Collectors.toList());
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = this.list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>()
                .in(!CollectionUtils.isEmpty(skuIds), SkuSaleAttrValueEntity::getSkuId, skuIds));

        Map<Long, List<SkuSaleAttrValueEntity>> collect = skuSaleAttrValueEntities.stream()
                .collect(Collectors.groupingBy(SkuSaleAttrValueEntity::getAttrId));
        collect.forEach((k, v) -> {
            SkuItemVo.ItemSaleAttrVo vo = new SkuItemVo.ItemSaleAttrVo();
            vo.setAttrId(k);

            SkuSaleAttrValueEntity skuSaleAttrValueEntity = v.stream()
                    .findFirst()
                    .orElseGet(SkuSaleAttrValueEntity::new);
            String attrName = skuSaleAttrValueEntity.getAttrName();
            vo.setAttrName(attrName);

            List<String> values = v.stream()
                    .map(SkuSaleAttrValueEntity::getAttrValue)
                    .distinct()
                    .collect(Collectors.toList());
            vo.setAttrValues(values);

            vos.add(vo);
        });

        return vos;
    }

}