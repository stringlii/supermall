package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.entity.SkuInfoEntity;
import com.litianyi.supermall.product.service.SkuInfoService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * sku信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * sku上架
     *
     * @param skuId skuId
     */
    @PostMapping("/{skuId}/up")
    public R skuUp(@PathVariable("skuId") Long skuId) throws Exception {
        skuInfoService.up(skuId);
        return R.ok();
    }

    /**
     * 更新上架的sku库存
     *
     * @param skuId skuId
     */
    @PostMapping("/{skuId}/update/stock")
    public R updateSkuStock(@PathVariable("skuId") Long skuId, @RequestParam Boolean hasStock) throws Exception {
        skuInfoService.updateStock(skuId, hasStock);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 信息
     */
    @RequestMapping("/get/{skuId}")
    public R getSku(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("data", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds) {
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
