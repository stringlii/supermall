package com.litianyi.supermall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 *
 * @author litianyi
 * @version 1.0
 * @date 2022/5/7 3:41 PM
 */
@Data
public class CartVo {

    private List<CartItem> items;

    /**
     * 商品数量
     */
    private Integer countItem;

    /**
     * 商品类型数量
     */
    private Integer countType;

    /**
     * 购物车总价
     */
    private BigDecimal amount;

    /**
     * 减免价格
     */
    private BigDecimal reduce = new BigDecimal("0");

    public Integer getCountItem() {
        countItem = 0;
        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                countItem += item.getCount();
            });
        }
        return countItem;
    }

    public Integer getCountType() {
        countType = 0;
        if (!CollectionUtils.isEmpty(items)) {
            countType = items.size();
        }
        return countType;
    }

    public BigDecimal getAmount() {
        amount = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                amount = amount.add(item.getTotalPrice());
            });
        }
        amount = amount.subtract(reduce);
        return amount;
    }

    /**
     * 购物项
     */
    @Data
    public static class CartItem {

        private Long skuId;

        private Boolean check = true;

        private String title;

        private String image;

        private List<String> skuAttr;

        private BigDecimal price = new BigDecimal("0");

        private Integer count = 1;

        private BigDecimal totalPrice;

        public BigDecimal getTotalPrice() {
            this.totalPrice = price.multiply(new BigDecimal(count.toString()));
            return totalPrice;
        }
    }
}
