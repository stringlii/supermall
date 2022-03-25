package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.common.constant.ProductConstant;
import com.litianyi.supermall.product.entity.AttrAttrgroupRelationEntity;
import com.litianyi.supermall.product.entity.AttrGroupEntity;
import com.litianyi.supermall.product.entity.CategoryEntity;
import com.litianyi.supermall.product.service.AttrAttrgroupRelationService;
import com.litianyi.supermall.product.service.AttrGroupService;
import com.litianyi.supermall.product.service.CategoryService;
import com.litianyi.supermall.product.vo.AttrGroupRelationVo;
import com.litianyi.supermall.product.vo.AttrRespVo;
import com.litianyi.supermall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.AttrDao;
import com.litianyi.supermall.product.entity.AttrEntity;
import com.litianyi.supermall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService relationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void save(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        baseMapper.insert(attrEntity);

        //保存属性分组关联信息
        if (attrVo.getAttrGroupId() != null
                && attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrSort(attrVo.getAttrSort());
            relationService.save(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId, String attrType) throws Exception {
        LambdaQueryWrapper<AttrEntity> lambdaWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isEmpty(attrType)) {
            throw new Exception("attrType不能为空");
        }
        lambdaWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getMsg().equals(attrType)
                ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            lambdaWrapper.and((wrapper) -> wrapper.eq(AttrEntity::getAttrId, key)
                    .or().like(AttrEntity::getAttrName, key)
                    .or().like(AttrEntity::getValueSelect, key));
        }

        if (catalogId != 0) {
            lambdaWrapper.eq(AttrEntity::getCatalogId, catalogId);
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), lambdaWrapper);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attrRespVos = records.stream().map((item) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(item, attrRespVo);

            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = relationService.getByAttrId(item.getAttrId());
                if (attrAttrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = this.categoryService.getById(item.getCatalogId());
            if (categoryEntity != null) {
                attrRespVo.setCatalogName(categoryEntity.getName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attrEntity.getAttrType()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = relationService.getByAttrId(attrId);
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            }
        }

        Long[] catalogPath = categoryService.findCatalogPath(attrEntity.getCatalogId());
        attrRespVo.setCatalogPath(catalogPath);
        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.updateById(attrEntity);

        Long attrGroupId = attr.getAttrGroupId();
        if (attrGroupId != null && ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attr.getAttrType()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrSort(attr.getAttrSort());

            long count = relationService.count(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
            if (count > 0) {
                relationService.update(attrAttrgroupRelationEntity, new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
            } else {
                relationService.save(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> listRelationAttr(Long attrgroupId) {
        List<AttrEntity> list = new ArrayList<>();
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = relationService.listByAttrgroupId(attrgroupId);
        List<Long> attrIds = attrgroupRelationEntityList.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(attrIds)) {
            list = baseMapper.selectBatchIds(attrIds);
        }
        return list;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> relationEntityList = Arrays.stream(vos).map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationService.deleteBatchRelation(relationEntityList);
    }

    @Override
    public PageUtils pageNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        Long catalogId = attrGroupEntity.getCatalogId();

        //找到该分类下已经被关联的属性
        List<AttrGroupEntity> attrGroupEntityList = attrGroupService.list(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatalogId, catalogId));
        List<Long> attrGroupIds = attrGroupEntityList.stream()
                .map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        List<Long> attrIds = relationService.listByAttrGroupIds(attrGroupIds).stream()
                .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>()
                .eq(AttrEntity::getCatalogId, catalogId)
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn(AttrEntity::getAttrId, attrIds);
        }
        if (StringUtils.isNotEmpty(key)) {
            wrapper.like(AttrEntity::getAttrName, key).eq(AttrEntity::getAttrId, key);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> listSearchAttrIdsByIds(List<Long> attrIds) {
        return this.baseMapper.listSearchAttrIdsByIds(attrIds);
    }

}