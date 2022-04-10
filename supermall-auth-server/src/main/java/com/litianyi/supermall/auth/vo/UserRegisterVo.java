package com.litianyi.supermall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/9 10:00 PM
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名必须提交")
    private String username;

    @NotEmpty(message = "密码必须提交")
    @Length(min = 6, message = "密码最少6位")
    private String password;

    @Pattern(regexp = "^[1][3578]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码必须提交")
    private String code;
}
