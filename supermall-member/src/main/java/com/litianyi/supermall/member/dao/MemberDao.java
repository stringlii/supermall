package com.litianyi.supermall.member.dao;

import com.litianyi.supermall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:39:44
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
