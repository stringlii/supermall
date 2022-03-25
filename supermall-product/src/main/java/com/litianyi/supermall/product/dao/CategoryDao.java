package com.litianyi.supermall.product.dao;

import com.litianyi.supermall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
