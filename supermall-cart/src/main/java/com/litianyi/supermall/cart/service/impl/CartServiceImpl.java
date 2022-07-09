package com.litianyi.supermall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.litianyi.common.constant.CartConstant;
import com.litianyi.common.to.product.SkuInfoTo;
import com.litianyi.common.to.product.SkuSaleAttrValueTO;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.cart.context.UserContextHandler;
import com.litianyi.supermall.cart.dto.UserInfoDTO;
import com.litianyi.supermall.cart.feign.ProductFeignService;
import com.litianyi.supermall.cart.service.CartService;
import com.litianyi.supermall.cart.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/7 4:33 PM
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ProductFeignService productFeignService;

    private static final String CART_PREFIX = "supermall:cart:";

    @Override
    public void add(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJson = (String) cartOps.get(skuId.toString());
        if (StringUtils.isNotEmpty(cartItemJson)) {
            CartVo.CartItem cartItem = JSON.parseObject(cartItemJson, CartVo.CartItem.class);
            cartItem.setCount(num + cartItem.getCount());
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return;
        }

        CartVo.CartItem cartItem = new CartVo.CartItem();
        CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
            R skuInfoR = productFeignService.getSkuInfo(skuId);
            if (skuInfoR.isSuccess()) {
                SkuInfoTo skuInfo = skuInfoR.getData("skuInfo", SkuInfoTo.class);
                cartItem.setCheck(true);
                cartItem.setSkuId(skuId);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
            } else {
                throw new RuntimeException("远程查询商品信息失败");
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> getSkuAttrFuture = CompletableFuture.runAsync(() -> {
            R saleAttrValueR = productFeignService.listSaleAttrValueBySkuId(skuId);
            if (saleAttrValueR.isSuccess()) {
                List<SkuSaleAttrValueTO> skuSaleAttrValueTOs = saleAttrValueR.getData(new TypeReference<List<SkuSaleAttrValueTO>>() {
                });
                List<String> attrs = skuSaleAttrValueTOs.stream().map(item -> {
                    String attrName = item.getAttrName();
                    String attrValue = item.getAttrValue();
                    return StringUtils.join(attrName, attrValue, " ");
                }).collect(Collectors.toList());
                cartItem.setSkuAttr(attrs);
            } else {
                throw new RuntimeException("远程查询商品信息失败");
            }
        }, threadPoolExecutor);

        CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrFuture).join();

        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public CartVo.CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJson = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(cartItemJson)) {
            return null;
        }
        return JSON.parseObject(cartItemJson, CartVo.CartItem.class);
    }

    @Override
    public CartVo getCart() {
        CartVo cartVo = new CartVo();

        UserInfoDTO userInfo = UserContextHandler.getUserInfo();
        if (userInfo.getUserId() != null) {
            //将临时购物车的商品合并到登录后的购物车
            //获取临时购物车
            List<CartVo.CartItem> tempCartItems = this.getCartItems(userInfo.getUserKey());
            if (!CollectionUtils.isEmpty(tempCartItems)) {
                //合并
                tempCartItems.forEach(item -> this.add(item.getSkuId(), item.getCount()));
                this.clearItem(userInfo.getUserKey());
            }
        }
        List<CartVo.CartItem> cartItems = this.getCartItems();
        cartVo.setItems(cartItems);

        return cartVo;
    }

    @Override
    public void clearItem(String userId) {
        redisTemplate.delete(CART_PREFIX + userId);
    }

    @Override
    public void checkItem(Long skuId, Boolean check) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartVo.CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCheck(check);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartVo.CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCount(num);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        cartOps.delete(skuId.toString());
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoDTO userInfo = UserContextHandler.getUserInfo();

        BoundHashOperations<String, Object, Object> hashOps;
        if (userInfo.getUserId() != null) {
            hashOps = this.getCartOps(userInfo.getUserId().toString());
        } else {
            hashOps = this.getCartOps(userInfo.getUserKey());
            hashOps.expire(CartConstant.COOKIE_MAX_AGE, TimeUnit.SECONDS);
        }

        return hashOps;
    }

    private BoundHashOperations<String, Object, Object> getCartOps(String userId) {
        return redisTemplate.boundHashOps(CART_PREFIX + userId);
    }

    private List<CartVo.CartItem> getCartItems() {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        List<Object> values = cartOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream()
                    .map(item -> JSON.parseObject((String) item, CartVo.CartItem.class))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<CartVo.CartItem> getCartItems(String userId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps(userId);
        List<Object> values = cartOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream()
                    .map(item -> JSON.parseObject((String) item, CartVo.CartItem.class))
                    .collect(Collectors.toList());
        }
        return null;
    }

}
