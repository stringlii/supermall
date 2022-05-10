package com.litianyi.supermall.product.feign;

import com.litianyi.common.to.search.SkuEsModel;
import com.litianyi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/30 12:31 PM
 */
@FeignClient("supermall-search")
public interface SearchFeignService {

    @PostMapping("/elasticsearch/save/product")
    R productUp(List<SkuEsModel> skuEsModels);

    @PostMapping("/elasticsearch/save/sku")
    R skuUp(@RequestBody SkuEsModel skuEsModel);

    @PostMapping("/elasticsearch/update/sku")
    R updateSku(@RequestBody SkuEsModel skuEsModel);
}
