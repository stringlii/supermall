package com.litianyi.supermall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.SpuImagesDao;
import com.litianyi.supermall.product.entity.SpuImagesEntity;
import com.litianyi.supermall.product.service.SpuImagesService;
import org.springframework.util.CollectionUtils;

@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }
        List<SpuImagesEntity> spuImagesEntityList = images.stream().map(item -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(id);
            spuImagesEntity.setImgUrl(item);
            String[] split = item.split("/");
            if (split.length > 0) {
                spuImagesEntity.setImgName(split[split.length - 1]);
            }
            return spuImagesEntity;
        }).collect(Collectors.toList());

        //设置第一张图片为默认图片
        spuImagesEntityList.get(0).setDefaultImg(1);

        this.saveBatch(spuImagesEntityList);
    }

}