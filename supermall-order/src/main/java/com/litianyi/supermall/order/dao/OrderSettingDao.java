package com.litianyi.supermall.order.dao;

import com.litianyi.supermall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:49:56
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
