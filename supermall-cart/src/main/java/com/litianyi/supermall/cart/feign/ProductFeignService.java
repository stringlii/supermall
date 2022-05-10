package com.litianyi.supermall.cart.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/9 3:44 PM
 */
@FeignClient("supermall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/list/{skuId}")
    R listSaleAttrValueBySkuId(@PathVariable Long skuId);
}
