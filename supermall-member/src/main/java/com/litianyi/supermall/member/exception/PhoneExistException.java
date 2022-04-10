package com.litianyi.supermall.member.exception;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/10 8:14 PM
 */
public class PhoneExistException extends Exception {
    public PhoneExistException() {
        super("手机号已存在");
    }
}
