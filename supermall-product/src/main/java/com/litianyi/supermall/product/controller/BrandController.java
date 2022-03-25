package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.litianyi.common.valid.group.AddGroup;
import com.litianyi.common.valid.group.UpdateGroup;
import com.litianyi.common.valid.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.entity.BrandEntity;
import com.litianyi.supermall.product.service.BrandService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 品牌
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/infos")
    public R infos(@RequestParam("brandIds") List<Long> brandIds) {
        List<BrandEntity> brandEntities = this.brandService.listByIds(brandIds);
        return R.ok().put("brand", brandEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody @Validated({AddGroup.class}) BrandEntity brand) {
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody @Validated({UpdateGroup.class}) BrandEntity brand) {
        brandService.updateCascade(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@RequestBody @Validated({UpdateStatusGroup.class}) BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
