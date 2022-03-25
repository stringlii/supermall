package com.litianyi.supermall.product.dao;

import com.litianyi.supermall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> listSearchAttrIdsByIds(@Param("attrIds") List<Long> attrIds);
}
