package com.litianyi.supermall.coupon.dao;

import com.litianyi.supermall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:21:58
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
