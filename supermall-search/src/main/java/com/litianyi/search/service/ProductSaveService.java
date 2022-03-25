package com.litianyi.search.service;

import com.litianyi.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/30 11:35 AM
 */
public interface ProductSaveService {

    boolean productUp(List<SkuEsModel> skuEsModels) throws IOException;

    boolean skuUp(SkuEsModel skuEsModel);

    boolean updateSku(SkuEsModel skuEsModel);
}
