package com.litianyi.supermall.member.vo;

import lombok.Data;

@Data
public class SocialUserInfoByWeiboVO {
    private long id;
    private String idstr;
    private String screen_name;
    private String name;
    private String province;
    private String city;
    private String location;
    private String description;
    private String url;
    private String profile_image_url;
    private String cover_image_phone;
    private String gender;
}