package com.litianyi.search.vo;

import com.litianyi.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/2/21 3:47 PM
 */
@Data
public class SearchResult {

    /**
     * 商品
     */
    private List<SkuEsModel> products;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long totalPages;

    private List<Integer> pageNavs;

    /**
     * 查询到的结果涉及到的所有分类
     */
    private List<CatalogVo> catalogs;

    /**
     * 查询到的结果涉及到的所有品牌
     */
    private List<BrandVo> brands;

    /**
     * 查询到的结果涉及到的所有属性
     */
    private List<AttrVo> attrs;

    //面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    //被选择的属性
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String value;
        private String link;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
