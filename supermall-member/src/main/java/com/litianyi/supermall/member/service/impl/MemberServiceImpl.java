package com.litianyi.supermall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.supermall.member.entity.MemberLevelEntity;
import com.litianyi.supermall.member.exception.PhoneExistException;
import com.litianyi.supermall.member.exception.UsernameExistException;
import com.litianyi.supermall.member.service.MemberLevelService;
import com.litianyi.supermall.member.to.MemberRegisterTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.member.dao.MemberDao;
import com.litianyi.supermall.member.entity.MemberEntity;
import com.litianyi.supermall.member.service.MemberService;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

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

}