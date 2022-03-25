package com.litianyi.supermall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.litianyi.supermall.product.entity.AttrEntity;
import com.litianyi.supermall.product.service.AttrAttrgroupRelationService;
import com.litianyi.supermall.product.service.AttrService;
import com.litianyi.supermall.product.service.CategoryService;
import com.litianyi.supermall.product.vo.AttrGroupRelationVo;
import com.litianyi.supermall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.product.entity.AttrGroupEntity;
import com.litianyi.supermall.product.service.AttrGroupService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 属性分组
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService relationService;

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> list = attrService.listRelationAttr(attrgroupId);
        return R.ok().put("data", list);
    }

    @GetMapping("/{catalogId}/withattr")
    public R listAttrGroupWithAttrsByCatalogId(@PathVariable Long catalogId) {
        List<AttrGroupWithAttrsVo> list = attrGroupService.listAttrGroupWithAttrsByCatalogId(catalogId);
        return R.ok().put("data", list);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.pageNoRelationAttr(params, attrgroupId);
        return R.ok().put("page", page);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {
        relationService.addRelation(vos);
        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrGroupService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catalogId}")
    public R listByCatalogId(@RequestParam Map<String, Object> params, @PathVariable("catalogId") Long catalogId) {
        PageUtils page = attrGroupService.queryPage(params, catalogId);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] catalogPath = categoryService.findCatalogPath(attrGroup.getCatalogId());
        attrGroup.setCatalogPath(catalogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
