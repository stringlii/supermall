package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.SkuInfoEntity;
import com.litianyi.supermall.product.service.SkuInfoService;
import com.litianyi.supermall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
        /*List<SkuItemVo.ItemSaleAttrVo> vos = new ArrayList<>();

        List<Long> skuIds = skuInfoService.listBySpuId(spuId).stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(Collectors.toList());
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = this.list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>()
                .in(!CollectionUtils.isEmpty(skuIds), SkuSaleAttrValueEntity::getSkuId, skuIds));

        Map<Long, List<SkuSaleAttrValueEntity>> attrIdMapSaleAttrValue = skuSaleAttrValueEntities.stream()
                .collect(Collectors.groupingBy(SkuSaleAttrValueEntity::getAttrId));
        attrIdMapSaleAttrValue.forEach((k, v) -> {
            SkuItemVo.ItemSaleAttrVo vo = new SkuItemVo.ItemSaleAttrVo();
            vo.setAttrId(k);

            SkuSaleAttrValueEntity skuSaleAttrValueEntity = v.stream()
                    .findFirst()
                    .orElseGet(SkuSaleAttrValueEntity::new);
            String attrName = skuSaleAttrValueEntity.getAttrName();
            vo.setAttrName(attrName);

            // 属性对应的skuId
            List<SkuItemVo.ItemSaleAttrVo.AttrValueWithSkuIdVo> values = new ArrayList<>();
            v.stream()
                    .collect(Collectors.groupingBy(SkuSaleAttrValueEntity::getAttrValue))
                    .forEach((key, value) -> {
                        SkuItemVo.ItemSaleAttrVo.AttrValueWithSkuIdVo attrValueWithSkuIdVo = new SkuItemVo.ItemSaleAttrVo.AttrValueWithSkuIdVo();
                        List<Long> collect = value.stream().map(SkuSaleAttrValueEntity::getSkuId).collect(Collectors.toList());
                        attrValueWithSkuIdVo.setAttrValue(key);
                        attrValueWithSkuIdVo.setSkuIds(collect);
                        values.add(attrValueWithSkuIdVo);
                    });
            vo.setAttrValues(values);

            vos.add(vo);
        });

        return vos;*/
        return this.baseMapper.getSaleAttrsBySpuId(spuId);
    }

}