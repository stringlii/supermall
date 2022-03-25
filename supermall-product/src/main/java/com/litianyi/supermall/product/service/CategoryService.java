package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.CategoryEntity;
import com.litianyi.supermall.product.vo.Catalog2Vo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品三级分类
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 树形结构的分类
     *
     * @return
     */
    List<CategoryEntity> listWithTree();

    boolean removeCategoryByIds(List<Long> asList);

    /**
     * 递归查找子菜单
     *
     * @param category     父菜单
     * @param categoryList 全部菜单列表
     * @return 子菜单列表
     */
    static List<CategoryEntity> getChildren(CategoryEntity category, List<CategoryEntity> categoryList) {
        if (category.getCatLevel() >= 3) {
            return new ArrayList<>();
        } else {
            return categoryList.stream()
                    .filter(item -> Objects.equals(item.getParentCid(), category.getCatId()))
                    .peek(item -> item.setChildren(getChildren(item, categoryList)))
                    .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 找到catalogId的完整路径
     *
     * @param catalogId
     * @return [父/子/子]
     */
    Long[] findCatalogPath(Long catalogId);

    default List<Long> findParentPath(Long catalogId) {
        List<Long> path = new ArrayList<>();
        path.add(catalogId);
        CategoryEntity category = this.getById(catalogId);
        if (category.getParentCid() != 0) {
            path.addAll(findParentPath(category.getParentCid()));
        }
        return path;
    }

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> listLevel1Categories();

    Map<String, List<Catalog2Vo>> getCatalogJson();

}

