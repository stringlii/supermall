package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.AttrAttrgroupRelationEntity;
import com.litianyi.supermall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AttrAttrgroupRelationEntity getByAttrId(Long attrId);

    List<AttrAttrgroupRelationEntity> listByAttrgroupId(Long attrgroupId);

    void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntityList);

    List<AttrAttrgroupRelationEntity> listByAttrGroupIds(List<Long> attrGroupIds);

    void addRelation(List<AttrGroupRelationVo> vos);

    List<AttrAttrgroupRelationEntity> listByAttrgroupIds(List<Long> attrGroupIds);
}

