package com.litianyi.search.feign;

import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("supermall-product")
public interface ProductFeignService {

    @RequestMapping("/product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    R brandInfos(@RequestParam("brandIds") List<Long> brandIds);

    @GetMapping("/product/category/info/{catId}")
    R categoryInfo(@PathVariable("catId") Long catId);
}
