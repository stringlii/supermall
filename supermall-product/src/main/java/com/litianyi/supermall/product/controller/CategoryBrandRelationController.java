package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.entity.BrandEntity;
import com.litianyi.supermall.product.vo.BrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.entity.CategoryBrandRelationEntity;
import com.litianyi.supermall.product.service.CategoryBrandRelationService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 品牌分类关联
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 当前品牌关联的分类
     */
    @GetMapping("/catalog/list")
    public R catalogList(@RequestParam Long brandId) {
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getBrandId, brandId));

        return R.ok().put("data", list);
    }

    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam Long catId) {
        List<BrandEntity> brandEntities = categoryBrandRelationService.listBrandsByCatId(catId);
        List<BrandVo> brandVoList = brandEntities.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            BeanUtils.copyProperties(item, brandVo);
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", brandVoList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {


        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
