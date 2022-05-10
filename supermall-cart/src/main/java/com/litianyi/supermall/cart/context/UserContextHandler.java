package com.litianyi.supermall.cart.context;

import com.litianyi.supermall.cart.dto.UserInfoDTO;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/8 8:38 PM
 */
public class UserContextHandler {

    public static ThreadLocal<UserInfoDTO> userInfo = new ThreadLocal<>();

    public static void setUserInfo(UserInfoDTO userInfo) {
        UserContextHandler.userInfo.set(userInfo);
    }

    public static UserInfoDTO getUserInfo() {
        return UserContextHandler.userInfo.get();
    }

    public static void removeUserInfo() {
        UserContextHandler.userInfo.remove();
    }
}
