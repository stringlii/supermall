package com.litianyi.supermall.auth.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/9 2:21 PM
 */
@FeignClient("supermall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping("/sms/send/code")
    R sendSms(@RequestParam String phone, @RequestParam String code);
}
