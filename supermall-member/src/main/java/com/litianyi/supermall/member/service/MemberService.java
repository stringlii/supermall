package com.litianyi.supermall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.member.entity.MemberEntity;
import com.litianyi.supermall.member.exception.PhoneExistException;
import com.litianyi.supermall.member.exception.UsernameExistException;
import com.litianyi.supermall.member.to.MemberRegisterTo;

import java.util.Map;

/**
 * 会员
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:39:44
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterTo to) throws PhoneExistException, UsernameExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    void checkPhoneUnique(String phone) throws PhoneExistException;
}

