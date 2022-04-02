package com.litianyi.supermall.product.vo;

import com.litianyi.supermall.product.entity.SkuImagesEntity;
import com.litianyi.supermall.product.entity.SkuInfoEntity;
import com.litianyi.supermall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/3/31 11:43 AM
 */
@Data
public class SkuItemVo {

    // sku 基本信息 pms_sku_info
    private SkuInfoEntity info;

    // sku 图片信息 pms_sku_images
    private List<SkuImagesEntity> images;

    // spu 销售属性组合
    private List<ItemSaleAttrVo> saleAttrs;

    // spu 介绍
    private SpuInfoDescEntity desc;

    // spu 规格参数信息
    private List<ItemAttrGroupVo> groupAttrs;

    @Data
    public static class ItemSaleAttrVo {

        private Long attrId;

        private String attrName;

        private List<String> attrValues;
    }

    @Data
    public static class ItemAttrGroupVo {

        private String groupName;

        private List<BaseAttrVo> attrs;

        @Data
        public static class BaseAttrVo {

            private String attrName;

            private String attrValues;
        }
    }
}
