package com.litianyi.supermall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:21:58
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

