package com.litianyi.supermall.product.dao;

import com.litianyi.supermall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litianyi.supermall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.ItemAttrGroupVo> listAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId,
                                                                  @Param("catalogId") Long catalogId);
}
