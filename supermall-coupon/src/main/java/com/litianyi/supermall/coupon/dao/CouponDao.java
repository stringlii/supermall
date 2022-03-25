package com.litianyi.supermall.coupon.dao;

import com.litianyi.supermall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:21:58
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
