package com.litianyi.supermall.order.dao;

import com.litianyi.supermall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:49:56
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
