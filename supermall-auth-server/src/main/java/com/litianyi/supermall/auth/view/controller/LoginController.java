package com.litianyi.supermall.auth.view.controller;

import com.litianyi.common.constant.DomainConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/7 3:38 PM
 */
@Controller
public class LoginController {


//    @GetMapping("/login.html")
//    public String loginPage() {
//        return "login";
//    }
//
//    @GetMapping("/register.html")
//    public String registerPage() {
//        return "register";
//    }

    @PostMapping("/register")
    public String register() {
        return "redirect:/login.html";
    }
}
