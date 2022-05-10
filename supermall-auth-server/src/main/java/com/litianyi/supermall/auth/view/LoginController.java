package com.litianyi.supermall.auth.view;

import com.litianyi.common.constant.AuthServerConstant;
import com.litianyi.common.constant.UrlConstant;
import com.litianyi.common.to.member.MemberTo;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.auth.feign.MemberFeignService;
import com.litianyi.supermall.auth.vo.UserLoginVo;
import com.litianyi.supermall.auth.vo.UserRegisterVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/7 3:38 PM
 */
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        if (session.getAttribute(AuthServerConstant.LOGIN_USER) == null) {
            return "login";
        }
        return "redirect:" + UrlConstant.SUPERMALL;
    }

    /**
     * @param attributes 模拟重定向携带数据
     */
    @PostMapping("/register")
    public String register(@Validated UserRegisterVo vo, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage,
                            (v1, v2) -> v2));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:" + UrlConstant.SUPERMALL_AUTH + "/register.html";
        }

        String code = vo.getCode();
        String oldCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.isNotEmpty(oldCode) && code.equals(oldCode.split("_")[0])) {
            redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
            R registerR = memberFeignService.register(vo);
            if (!registerR.isSuccess()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", registerR.getMessage());
                attributes.addFlashAttribute("errors", errors);
                return "redirect:" + UrlConstant.SUPERMALL_AUTH + "/register.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:" + UrlConstant.SUPERMALL_AUTH + "/register.html";
        }

        return "redirect:" + UrlConstant.SUPERMALL_AUTH + "/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {
        R loginR = memberFeignService.login(vo);
        if (!loginR.isSuccess()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("errors", loginR.getMessage());
            attributes.addFlashAttribute("errors", errors);
            return "redirect:" + UrlConstant.SUPERMALL_AUTH + "/login.html";
        }

        MemberTo memberTo = loginR.getData(MemberTo.class);
        session.setAttribute(AuthServerConstant.LOGIN_USER, memberTo);
        return "redirect:" + UrlConstant.SUPERMALL;
    }
}
