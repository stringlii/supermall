package com.litianyi.supermall.cart.service;

import com.litianyi.supermall.cart.vo.CartVo;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/7 4:33 PM
 */
public interface CartService {

    void add(Long skuId, Integer num);

    CartVo.CartItem getCartItem(Long skuId);

    CartVo getCart();

    /**
     * 清空购物车
     *
     * @param userId 购物车key
     */
    void clearItem(String userId);

    /**
     * 勾选购物项
     */
    void checkItem(Long skuId, Boolean check);

    /**
     * 改变购物车商品数量
     */
    void countItem(Long skuId, Integer num);

    /**
     * 删除购物项
     */
    void deleteItem(Long skuId);
}
