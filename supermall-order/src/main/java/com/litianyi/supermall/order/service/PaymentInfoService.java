package com.litianyi.supermall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:49:56
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

