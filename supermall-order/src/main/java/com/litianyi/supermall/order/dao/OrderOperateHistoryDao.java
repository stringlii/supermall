package com.litianyi.supermall.order.dao;

import com.litianyi.supermall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:49:56
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
