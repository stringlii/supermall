package com.litianyi.supermall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.BrandDao;
import com.litianyi.supermall.product.entity.BrandEntity;
import com.litianyi.supermall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>().lambda();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and((lambda) -> lambda
                    .eq(BrandEntity::getBrandId, key)
                    .or()
                    .like(BrandEntity::getName, key)
            );
        }
        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void updateCascade(BrandEntity brand) {
        baseMapper.updateById(brand);
        if (StringUtils.isNotEmpty(brand.getName())) {
            //同步更新其他冗余存储了品牌名的表
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            //todo 更新其他表
        }
    }

}