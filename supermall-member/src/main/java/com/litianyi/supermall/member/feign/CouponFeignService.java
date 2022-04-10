package com.litianyi.supermall.member.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/2 7:31 PM
 */
@FeignClient("supermall-coupon")
public interface CouponFeignService {
}
