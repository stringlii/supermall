package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.litianyi.supermall.product.entity.BrandEntity;
import com.litianyi.supermall.product.entity.CategoryEntity;
import com.litianyi.supermall.product.service.BrandService;
import com.litianyi.supermall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.CategoryBrandRelationDao;
import com.litianyi.supermall.product.entity.CategoryBrandRelationEntity;
import com.litianyi.supermall.product.service.CategoryBrandRelationService;
import org.springframework.util.CollectionUtils;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandService.getById(brandId);
        CategoryEntity categoryEntity = categoryService.getById(catelogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        baseMapper.insert(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String brandName) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(brandName);
        baseMapper.update(relationEntity, new LambdaUpdateWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getBrandId, brandId));
    }

    @Override
    public void updateCategory(Long catId, String catName) {
        baseMapper.updateCategory(catId, catName);
    }

    @Override
    public List<BrandEntity> listBrandsByCatId(Long catId) {
        List<BrandEntity> list = new ArrayList<>();
        List<CategoryBrandRelationEntity> relationEntityList = this.list(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getCatelogId, catId));
        List<Long> brandIds = relationEntityList.stream()
                .map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(brandIds)) {
            list = brandService.listByIds(brandIds);
        }
        return list;
    }

}