package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.entity.CategoryEntity;
import com.litianyi.supermall.product.service.CategoryService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 商品三级分类
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list/tree")
    public R listWithTree() {
        List<CategoryEntity> entityList = this.categoryService.listWithTree();
        return R.ok().put("data", entityList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @PostMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] categories) {
        categoryService.updateBatchById(Arrays.asList(categories));

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        categoryService.removeCategoryByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
