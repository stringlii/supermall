package com.litianyi.supermall.auth.feign;

import com.litianyi.common.utils.R;
import com.litianyi.supermall.auth.vo.UserLoginVo;
import com.litianyi.supermall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/10 9:40 PM
 */
@FeignClient("supermall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
