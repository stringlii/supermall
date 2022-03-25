package com.litianyi.supermall.product.dao;

import com.litianyi.supermall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("relationEntityList") List<AttrAttrgroupRelationEntity> relationEntityList);
}
