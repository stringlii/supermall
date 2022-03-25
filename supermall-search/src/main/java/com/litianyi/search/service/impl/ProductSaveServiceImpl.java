package com.litianyi.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.litianyi.common.to.es.SkuEsModel;
import com.litianyi.search.constant.EsConstant;
import com.litianyi.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.litianyi.search.config.ElasticSearchConfiguration.COMMON_OPTIONS;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/30 11:36 AM
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restClient;

    @Override
    public boolean productUp(List<SkuEsModel> skuEsModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        skuEsModels.forEach(sku -> {
            bulkRequest.add(new IndexRequest(EsConstant.PRODUCT_INDEX)
                    .id(sku.getSkuId().toString())
                    .source(JSON.toJSONString(sku), XContentType.JSON));
        });
        BulkResponse bulk = restClient.bulk(bulkRequest, COMMON_OPTIONS);

        Map<String, String> failureMap = new HashMap<>();
        Map<String, String> successMap = new HashMap<>();
        Arrays.stream(bulk.getItems()).forEach(item -> {
            BulkItemResponse.Failure failure = item.getFailure();
            if (failure != null) {
                failureMap.put(item.getId(), failure.getMessage());
            } else {
                successMap.put(item.getId(), "成功");
            }
        });
        log.info("商品上架错误: {}", failureMap);
        log.info("商品上架成功: {}", successMap);

        return !bulk.hasFailures();
    }

    @Override
    public boolean skuUp(SkuEsModel skuEsModel) {
        IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX)
                .id(skuEsModel.getSkuId().toString())
                .source(JSON.toJSONString(skuEsModel), XContentType.JSON);
        try {
            IndexResponse index = restClient.index(indexRequest, COMMON_OPTIONS);
            RestStatus status = index.status();
            return status == RestStatus.OK;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateSku(SkuEsModel skuEsModel) {
        UpdateRequest updateRequest = new UpdateRequest(EsConstant.PRODUCT_INDEX, skuEsModel.getSkuId().toString());
        UpdateRequest indexRequest = updateRequest.doc(JSON.toJSONString(skuEsModel), XContentType.JSON);
        try {
            UpdateResponse update = restClient.update(indexRequest, COMMON_OPTIONS);
            RestStatus status = update.status();
            return status == RestStatus.OK;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
