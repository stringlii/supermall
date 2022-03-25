package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.AttrEntity;
import com.litianyi.supermall.product.service.AttrService;
import com.litianyi.supermall.product.vo.spu.save.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.ProductAttrValueDao;
import com.litianyi.supermall.product.entity.ProductAttrValueEntity;
import com.litianyi.supermall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttr(Long id, List<BaseAttrs> baseAttrs) {
        List<ProductAttrValueEntity> baseAttrValueEntityList = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity baseAttrValueEntity = new ProductAttrValueEntity();
            baseAttrValueEntity.setAttrId(item.getAttrId());
            baseAttrValueEntity.setAttrValue(item.getAttrValues());
            baseAttrValueEntity.setQuickShow(item.getShowDesc());
            baseAttrValueEntity.setSpuId(id);
            AttrEntity attrEntity = attrService.getById(item.getAttrId());
            if (attrEntity != null) {
                baseAttrValueEntity.setAttrName(attrEntity.getAttrName());
            }
            return baseAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(baseAttrValueEntityList);
    }

    @Override
    public List<ProductAttrValueEntity> ListBySpuId(Long spuId) {
        return this.list(new LambdaQueryWrapper<ProductAttrValueEntity>()
                .eq(ProductAttrValueEntity::getSpuId, spuId));
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> list) {
        this.remove(new LambdaQueryWrapper<ProductAttrValueEntity>()
                .eq(ProductAttrValueEntity::getSpuId, spuId));
        List<ProductAttrValueEntity> collect = list.stream().peek(item -> {
            item.setSpuId(spuId);
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}