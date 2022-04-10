package com.litianyi.supermall.member.exception;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/10 8:13 PM
 */
public class UsernameExistException extends Exception {
    public UsernameExistException() {
        super("用户名已存在");
    }
}
