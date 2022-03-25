package com.litianyi.supermall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:21:58
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

