package com.litianyi.supermall.cart.controller;

import com.litianyi.common.constant.UrlConstant;
import com.litianyi.supermall.cart.context.UserContextHandler;
import com.litianyi.supermall.cart.dto.UserInfoDTO;
import com.litianyi.supermall.cart.service.CartService;
import com.litianyi.supermall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/7 5:20 PM
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 购物车列表页
     */
    @GetMapping("/cart.html")
    public String cartListPage() {

        UserInfoDTO userInfo = UserContextHandler.getUserInfo();
        return "cartList";
    }

    /**
     * 添加商品到购物车
     */
    @GetMapping("/add")
    public String add(@RequestParam Long skuId, @RequestParam Integer num, RedirectAttributes redirectAttributes) {

        cartService.add(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:" + UrlConstant.SUPERMALL_CART + "/addToCart.html";
    }

    /**
     * 防止刷新页面重复添加商品
     */
    @GetMapping("/addToCart.html")
    public String addToCart(@RequestParam Long skuId, Model model) {
        CartVo.CartItem cartItem = cartService.getAddToCart(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }
}
