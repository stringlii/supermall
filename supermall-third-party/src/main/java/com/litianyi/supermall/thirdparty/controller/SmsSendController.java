package com.litianyi.supermall.thirdparty.controller;

import com.litianyi.common.utils.R;
import com.litianyi.supermall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/9 2:01 PM
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    private SmsComponent smsComponent;

    @GetMapping("/send/code")
    public R sendSms(@RequestParam String phone, @RequestParam String code) throws Exception {
        smsComponent.sendCode(phone, code);
        return R.ok();
    }
}
