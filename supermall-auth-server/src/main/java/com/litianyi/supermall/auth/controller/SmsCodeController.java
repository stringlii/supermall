package com.litianyi.supermall.auth.controller;

import com.litianyi.common.constant.AuthServerConstant;
import com.litianyi.common.constant.BizCodeEnum;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.auth.feign.ThirdPartyFeignService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/9 9:54 PM
 */
@RestController
@RequestMapping("/sms")
public class SmsCodeController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/send/code")
    public R sendCode(@RequestParam String phone) {
        //接口防刷
        String oldCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotEmpty(oldCode)) {
            long l = Long.parseLong(oldCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //60秒内不能再发
                return R.error(BizCodeEnum.VALID_SMS_CODE_EXCEPTION);
            }
        }

        //验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                code + "_" + System.currentTimeMillis(),
                10,
                TimeUnit.MINUTES);

        thirdPartyFeignService.sendSms(phone, code.toString());
        return R.ok();
    }
}
