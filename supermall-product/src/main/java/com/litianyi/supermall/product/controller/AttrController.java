package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.litianyi.supermall.product.entity.ProductAttrValueEntity;
import com.litianyi.supermall.product.service.ProductAttrValueService;
import com.litianyi.supermall.product.vo.AttrRespVo;
import com.litianyi.supermall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.service.AttrService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 商品属性
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.ListBySpuId(spuId);
        return R.ok().put("data", list);
    }

    /**
     * 根据分组查询属性列表
     */
    @RequestMapping("/{attrType}/list/{catalogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params, @PathVariable("catalogId") Long catalogId,
                          @PathVariable("attrType") String attrType) throws Exception {
        PageUtils page = attrService.queryBaseAttrPage(params, catalogId, attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.save(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> list) {
        productAttrValueService.updateSpuAttr(spuId, list);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
