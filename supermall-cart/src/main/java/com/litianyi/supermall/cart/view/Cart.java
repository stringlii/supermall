package com.litianyi.supermall.cart.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/3 2:23 PM
 */
@Controller
public class Cart {

    @GetMapping("/cart.html")
    public String cart() {
        return "cartList";
    }

    @GetMapping("/success.html")
    public String success() {
        return "success";
    }
}
