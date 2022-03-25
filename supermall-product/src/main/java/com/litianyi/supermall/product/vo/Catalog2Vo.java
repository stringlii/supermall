package com.litianyi.supermall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @description 首页使用的二级分类vo
 * @date 2022/2/1 10:24 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo {
    /**
     * 一级分类id
     */
    private Long catalog1Id;
    /**
     * 二级分类id
     */
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 三级子分类
     */
    private List<Catalog3Vo> catalog3List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo {
        /**
         * 二级分类id
         */
        private Long catalog1Id;
        /**
         * 三级分类id
         */
        private Long id;
        /**
         * 分类名称
         */
        private String name;
    }
}
