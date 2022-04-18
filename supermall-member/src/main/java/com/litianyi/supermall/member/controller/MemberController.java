package com.litianyi.supermall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.litianyi.common.constant.BizCodeEnum;
import com.litianyi.supermall.member.exception.PhoneExistException;
import com.litianyi.supermall.member.exception.UsernameExistException;
import com.litianyi.supermall.member.to.MemberLoginTo;
import com.litianyi.supermall.member.to.MemberRegisterTo;
import com.litianyi.supermall.member.to.SocialUserByWeibo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.litianyi.supermall.member.entity.MemberEntity;
import com.litianyi.supermall.member.service.MemberService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.R;

/**
 * 会员
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:39:44
 */
@RestController
@RequestMapping("member/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/oauth/login")
    public R oauthLogin(@RequestBody SocialUserByWeibo socialUserByWeibo) {
        MemberEntity memberEntity = memberService.oauthLogin(socialUserByWeibo);
        if (memberEntity == null) {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_OAUTH_INVALID_EXCEPTION);
        }
        return R.ok().setData(memberEntity);
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterTo to) {
        try {
            memberService.register(to);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION);
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION);
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginTo to) {
        MemberEntity memberEntity = memberService.oauthLogin(to);
        if (memberEntity == null) {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID_EXCEPTION);
        }
        return R.ok().setData(memberEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
