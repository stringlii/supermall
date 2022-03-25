package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.ProductAttrValueEntity;
import com.litianyi.supermall.product.vo.spu.save.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttr(Long id, List<BaseAttrs> baseAttrs);

    List<ProductAttrValueEntity> ListBySpuId(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> list);
}

