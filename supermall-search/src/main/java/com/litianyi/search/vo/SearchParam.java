package com.litianyi.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @description 查询条件
 * @date 2022/2/21 2:57 PM
 */
@Data
public class SearchParam {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 品牌
     */
    private List<Long> brandId;

    /**
     * 属性
     */
    private List<String> attrs;

    /**
     * 排序
     * saleCount_asc/desc
     * skuPrice_asc/desc
     * hostScore_asc/desc
     */
    private String sort;

    /**
     * 是否有货 0无货，1有货
     */
    private Integer hasStock;

    /**
     * skuPrice=1_500/_500/500_
     */
    private String skuPriceInterval;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;
}
