package com.litianyi.supermall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Multimap;
import com.litianyi.supermall.member.entity.MemberLevelEntity;
import com.litianyi.supermall.member.exception.PhoneExistException;
import com.litianyi.supermall.member.exception.UsernameExistException;
import com.litianyi.supermall.member.service.MemberLevelService;
import com.litianyi.supermall.member.to.MemberLoginTo;
import com.litianyi.supermall.member.to.MemberRegisterTo;
import com.litianyi.supermall.member.to.SocialUserByWeibo;
import com.litianyi.supermall.member.vo.SocialUserInfoByWeiboVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.member.dao.MemberDao;
import com.litianyi.supermall.member.entity.MemberEntity;
import com.litianyi.supermall.member.service.MemberService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    private static final String WEIBO_USER_INFO_URL = "https://api.weibo.com/2/users/show.json?access_token={access_token}&uid={uid}";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterTo to) throws PhoneExistException, UsernameExistException {
        checkPhoneUnique(to.getPhone());
        checkUsernameUnique(to.getUsername());

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(to.getUsername());
        memberEntity.setMobile(to.getPhone());
        memberEntity.setNickname(to.getUsername());

        MemberLevelEntity levelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(to.getPassword());
        memberEntity.setPassword(encode);

        this.save(memberEntity);
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        long count = this.count(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, username));
        if (count > 0)
            throw new UsernameExistException();
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        long count = this.count(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getMobile, phone));
        if (count > 0)
            throw new PhoneExistException();
    }

    @Override
    public MemberEntity oauthLogin(MemberLoginTo to) {
        MemberEntity memberEntity = this.getOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, to.getLoginAcct())
                .or()
                .eq(MemberEntity::getMobile, to.getLoginAcct()));
        if (memberEntity == null) {
            return null;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(to.getPassword(), memberEntity.getPassword())) {
            return null;
        }

        return memberEntity;
    }

    @Override
    public MemberEntity oauthLogin(SocialUserByWeibo socialUserByWeibo) {
        String uid = socialUserByWeibo.getUid();
        MemberEntity memberEntity = this.getOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getSocialUid, uid));
        if (memberEntity == null) {
            //查询当前社交用户的信息
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> map = new HashMap<>();
            map.put("access_token", socialUserByWeibo.getAccess_token());
            map.put("uid", socialUserByWeibo.getUid());
            SocialUserInfoByWeiboVO socialUserInfoByWeiboVO = restTemplate.getForObject(WEIBO_USER_INFO_URL, SocialUserInfoByWeiboVO.class, map);
            if (socialUserInfoByWeiboVO == null) {
                return null;
            }
            memberEntity = new MemberEntity();
            memberEntity.setNickname(socialUserInfoByWeiboVO.getName());
            memberEntity.setGender("m".equals(socialUserInfoByWeiboVO.getGender()) ? 1 : 0);
            memberEntity.setHeader(socialUserInfoByWeiboVO.getProfile_image_url());
            memberEntity.setAccessToken(socialUserByWeibo.getAccess_token());
            memberEntity.setExpiresIn(socialUserByWeibo.getExpires_in());
            memberEntity.setSocialUid(socialUserByWeibo.getUid());
            memberEntity.setLevelId(1L);
            this.save(memberEntity);
        } else {
            MemberEntity memberUpdate = new MemberEntity();
            memberUpdate.setId(memberEntity.getId());
            memberUpdate.setAccessToken(socialUserByWeibo.getAccess_token());
            memberUpdate.setExpiresIn(socialUserByWeibo.getExpires_in());
            this.updateById(memberUpdate);
        }

        return memberEntity;
    }

}