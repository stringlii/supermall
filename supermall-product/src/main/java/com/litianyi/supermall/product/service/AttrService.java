package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.AttrEntity;
import com.litianyi.supermall.product.vo.AttrGroupRelationVo;
import com.litianyi.supermall.product.vo.AttrRespVo;
import com.litianyi.supermall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId, String attrType) throws Exception;

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> listRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取当前属性分组没有关联的属性
     *
     * @param params      分页参数
     * @param attrgroupId 属性分组id
     */
    PageUtils pageNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 从指定的属性ids里，挑出可检索属性
     *
     * @param attrIds attrIds
     * @return attrIds
     */
    List<Long> listSearchAttrIdsByIds(List<Long> attrIds);
}

