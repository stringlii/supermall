package com.litianyi.search.controller;

import com.litianyi.common.constant.BizCodeEnum;
import com.litianyi.common.to.es.SkuEsModel;
import com.litianyi.common.utils.R;
import com.litianyi.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/30 11:26 AM
 */
@RequestMapping("elasticsearch")
@RestController
@Slf4j
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    /**
     * 上架商品
     */
    @PostMapping("/save/product")
    public R productUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean isSuccess;
        try {
            isSuccess = productSaveService.productUp(skuEsModels);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            isSuccess = false;
        }
        return isSuccess ? R.ok() : R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
    }

    @PostMapping("/save/sku")
    public R skuUp(@RequestBody SkuEsModel skuEsModel) {
        boolean isSuccess;
        try {
            isSuccess = productSaveService.skuUp(skuEsModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            isSuccess = false;
        }
        return isSuccess ? R.ok() : R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
    }

    @PostMapping("/update/sku")
    public R updateSku(@RequestBody SkuEsModel skuEsModel) {
        boolean isSuccess;
        try {
            isSuccess = productSaveService.updateSku(skuEsModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            isSuccess = false;
        }
        return isSuccess ? R.ok() : R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
    }
}
