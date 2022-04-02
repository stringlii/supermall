package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.AttrAttrgroupRelationEntity;
import com.litianyi.supermall.product.entity.AttrEntity;
import com.litianyi.supermall.product.entity.ProductAttrValueEntity;
import com.litianyi.supermall.product.service.AttrAttrgroupRelationService;
import com.litianyi.supermall.product.service.AttrService;
import com.litianyi.supermall.product.service.ProductAttrValueService;
import com.litianyi.supermall.product.vo.AttrGroupWithAttrsVo;
import com.litianyi.supermall.product.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

import com.litianyi.supermall.product.dao.AttrGroupDao;
import com.litianyi.supermall.product.entity.AttrGroupEntity;
import com.litianyi.supermall.product.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(lambdaWrapper -> lambdaWrapper.eq(AttrGroupEntity::getAttrGroupId, key)
                    .or().like(AttrGroupEntity::getAttrGroupName, key));
        }

        if (catalogId != 0) {
            wrapper.eq(AttrGroupEntity::getCatalogId, catalogId);
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrsVo> listAttrGroupWithAttrsByCatalogId(Long catalogId) {
        List<AttrGroupEntity> attrGroupEntityList = this.list(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatalogId, catalogId));

        return attrGroupEntityList.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity, vo);
            List<AttrEntity> attrEntityList = attrService.listRelationAttr(attrGroupEntity.getAttrGroupId());
            vo.setAttrs(attrEntityList);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SkuItemVo.ItemAttrGroupVo> listAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.listAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}