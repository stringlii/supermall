package com.litianyi.supermall.cart.service;

import com.litianyi.supermall.cart.vo.CartVo;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/7 4:33 PM
 */
public interface CartService {

    void add(Long skuId, Integer num);

    CartVo.CartItem getAddToCart(Long skuId);
}
