package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 同步更新冗余字段
     */
    void updateCascade(BrandEntity brand);
}

