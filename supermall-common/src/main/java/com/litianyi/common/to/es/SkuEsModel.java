package com.litianyi.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/29 6:55 PM
 */
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;

    private Long brandId;
    private String brandName;
    private String brandImg;
    private Long catalogId;
    private String catalogName;

    private List<Attr> attrs;

    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
