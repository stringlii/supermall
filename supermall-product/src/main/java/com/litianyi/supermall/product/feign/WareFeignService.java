package com.litianyi.supermall.product.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/29 8:49 PM
 */
@FeignClient("supermall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R hasStock(@RequestBody List<Long> skuIds);
}
