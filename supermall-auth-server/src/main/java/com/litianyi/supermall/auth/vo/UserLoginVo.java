package com.litianyi.supermall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/11 11:32 AM
 */
@Data
public class UserLoginVo {

    @NotEmpty
    private String loginAcct;

    @NotEmpty
    private String password;
}
