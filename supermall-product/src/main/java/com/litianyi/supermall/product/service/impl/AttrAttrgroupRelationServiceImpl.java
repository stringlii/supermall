package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.AttrGroupEntity;
import com.litianyi.supermall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
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

import com.litianyi.supermall.product.dao.AttrAttrgroupRelationDao;
import com.litianyi.supermall.product.entity.AttrAttrgroupRelationEntity;
import com.litianyi.supermall.product.service.AttrAttrgroupRelationService;
import org.springframework.util.CollectionUtils;

@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public AttrAttrgroupRelationEntity getByAttrId(Long attrId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrId, attrId));
    }

    @Override
    public List<AttrAttrgroupRelationEntity> listByAttrgroupId(Long attrgroupId) {
        return baseMapper.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
    }

    @Override
    public void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntityList) {
        baseMapper.deleteBatchRelation(relationEntityList);
    }

    @Override
    public List<AttrAttrgroupRelationEntity> listByAttrGroupIds(List<Long> attrGroupIds) {
        List<AttrAttrgroupRelationEntity> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attrGroupIds)) {
            list = baseMapper.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
        }
        return list;
    }

    @Override
    public void addRelation(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> relationEntityList = vos.stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(relationEntityList);
    }

    @Override
    public List<AttrAttrgroupRelationEntity> listByAttrgroupIds(List<Long> attrGroupIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
    }

}