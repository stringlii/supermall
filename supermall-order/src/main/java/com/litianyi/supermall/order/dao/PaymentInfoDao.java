package com.litianyi.supermall.order.dao;

import com.litianyi.supermall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:49:56
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
