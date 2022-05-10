package com.litianyi.supermall.cart.interceptor;

import com.litianyi.common.constant.AuthServerConstant;
import com.litianyi.common.constant.CartConstant;
import com.litianyi.common.constant.DomainConstant;
import com.litianyi.common.to.member.MemberTo;
import com.litianyi.supermall.cart.context.UserContextHandler;
import com.litianyi.supermall.cart.dto.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.UUID;

/**
 * 在执行目标方法之前，先判断用户的登录状态。
 *
 * @author litianyi
 * @version 1.0
 * @date 2022/5/8 3:33 PM
 */
public class CartInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UserInfoDTO userInfoDTO = new UserInfoDTO();

        HttpSession session = request.getSession();
        MemberTo memberTo = (MemberTo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberTo != null) {
            userInfoDTO.setUserId(memberTo.getId());
        }

        Cookie userKey = Arrays.stream(request.getCookies())
                .filter(item -> StringUtils.equals(item.getName(), CartConstant.TEMP_USER_COOKIE_NAME))
                .findFirst().orElse(null);
        if (userKey == null) {
            String uuid = UUID.randomUUID().toString();
            userInfoDTO.setUserKey(uuid);
            this.setUserCookie(response, uuid);
        } else {
            userInfoDTO.setUserKey(userKey.getValue());
        }

        UserContextHandler.setUserInfo(userInfoDTO);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void setUserCookie(HttpServletResponse response, String uuid) {
        Cookie userKey;
        userKey = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, uuid);
        userKey.setMaxAge(CartConstant.COOKIE_MAX_AGE);
        userKey.setDomain(DomainConstant.SUPERMALL);
        response.addCookie(userKey);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        UserContextHandler.removeUserInfo();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
