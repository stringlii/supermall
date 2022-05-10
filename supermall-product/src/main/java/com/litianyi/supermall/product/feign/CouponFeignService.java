package com.litianyi.supermall.product.feign;

import com.litianyi.common.to.product.SpuBoundTo;
import com.litianyi.common.to.product.SpuReductionTo;
import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/17 5:53 PM
 */
@FeignClient("supermall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SpuReductionTo spuReductionTo);
}
