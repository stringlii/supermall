package com.litianyi.supermall.auth.to;

import lombok.Data;

@Data
public class SocialUserByWeibo {

    private String access_token;

    private String remind_in;

    private Long expires_in;

    private String uid;

    private String isRealName;
}