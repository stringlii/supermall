package com.litianyi.supermall.ware.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/21 3:20 PM
 */
@FeignClient("supermall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/{skuId}/up")
    R skuUp(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/{skuId}/update/stock")
    R updateSkuStock(@PathVariable("skuId") Long skuId, @RequestParam Boolean hasStock);
}
