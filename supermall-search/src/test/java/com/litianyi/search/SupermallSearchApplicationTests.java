package com.litianyi.search;

import com.alibaba.fastjson.JSON;
import com.litianyi.search.config.ElasticSearchConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Slf4j
@SpringBootTest
class SupermallSearchApplicationTests {

    @Autowired
    RestHighLevelClient restClient;

    @Test
    void contextLoads() {
        System.out.println(restClient);
    }

    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setName("li");
        user.setAge(23);
        user.setGender("m");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse index = restClient.index(indexRequest, ElasticSearchConfiguration.COMMON_OPTIONS);
        System.out.println(index.toString());
    }

    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders
                        .matchQuery("address", "mill"))
                .aggregation(AggregationBuilders
                        .terms("ageAgg")
                        .field("age")
                        .size(10)
                        .subAggregation(AggregationBuilders
                                .avg("balanceAvgAgg")
                                .field("balance")))
                .aggregation(AggregationBuilders
                        .avg("balanceAvg")
                        .field("balance"));
        searchRequest.source(sourceBuilder);
        log.info("sourceBuilder:{}", sourceBuilder);

        SearchResponse response = restClient.search(searchRequest, ElasticSearchConfiguration.COMMON_OPTIONS);
        System.out.println(response);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits.getHits()) {
            log.info("hit: {}", JSON.parseObject(hit.getSourceAsString(), Account.class));
        }

        Aggregations aggregations = response.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String key = bucket.getKeyAsString();
            Avg balanceAvgAgg = bucket.getAggregations().get("balanceAvgAgg");
            log.info("age:{},num:{},balanceAvg:{}", key, bucket.getDocCount(), balanceAvgAgg.getValueAsString());
        }

        Avg balanceAvg = aggregations.get("balanceAvg");
        log.info("{}", balanceAvg.getValueAsString());
    }

    @Data
    static class User {
        private String name;
        private Integer age;
        private String gender;
    }

    @Data
    static class Account {
        private int accountNumber;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}
