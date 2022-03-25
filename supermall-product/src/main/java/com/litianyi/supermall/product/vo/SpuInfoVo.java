package com.litianyi.supermall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * spu信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@Data
public class SpuInfoVo {
    /**
     * 商品id
     */
    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 所属分类名称
     */
    private String catalogName;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     *
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 新建，1 - 上架，2 - 下架]
     */
    private Integer publishStatus;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private Date updateTime;

}
