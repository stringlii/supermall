package com.litianyi.supermall.auth.controller;

import com.litianyi.common.constant.DomainConstant;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.auth.config.WeiboOAuth2Properties;
import com.litianyi.supermall.auth.feign.MemberFeignService;
import com.litianyi.supermall.auth.to.SocialUserByWeibo;
import com.litianyi.supermall.auth.vo.MemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/14 23:21
 */
@Slf4j
@Controller
@EnableConfigurationProperties(WeiboOAuth2Properties.class)
public class OAuth2Controller {

    @Autowired
    private WeiboOAuth2Properties weiboOAuth2Properties;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam String code, HttpSession session) {
        //换取access token
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", weiboOAuth2Properties.getClientId());
        map.add("client_secret", weiboOAuth2Properties.getClientSecret());
        map.add("grant_type", weiboOAuth2Properties.getGrantType());
        map.add("redirect_uri", weiboOAuth2Properties.getRedirectUri());
        map.add("code", code);
        SocialUserByWeibo socialUser = restTemplate.postForObject(weiboOAuth2Properties.getWeiboAccessTokenUrl(), map, SocialUserByWeibo.class);
        if (socialUser == null) {
            return "redirect:" + DomainConstant.SUPERMALL_AUTH + "/login.html";
        }

        // 登录或注册
        R oauthR = memberFeignService.oauthLogin(socialUser);
        if (!oauthR.isSuccess()) {
            return "redirect:" + DomainConstant.SUPERMALL_AUTH + "/login.html";
        }
        MemberVo memberVo = oauthR.getData(MemberVo.class);
        session.setAttribute("loginUser", memberVo);
        return "redirect:" + DomainConstant.SUPERMALL;
    }
}
