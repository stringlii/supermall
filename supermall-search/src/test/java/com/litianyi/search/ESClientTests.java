package com.litianyi.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/28 10:11 AM
 */
@Slf4j
@SpringBootTest
public class ESClientTests {

    @Autowired
    ElasticsearchClient client;

    @Test
    void indexData() throws IOException {
        SupermallSearchApplicationTests.User user = new SupermallSearchApplicationTests.User();
        user.setName("li");
        user.setAge(22);
        user.setGender("m");
        IndexResponse response = client.index(
                new co.elastic.clients.elasticsearch.core.IndexRequest.Builder<SupermallSearchApplicationTests.User>()
                        .index("users")
                        .id("2")
                        .document(user)
                        .build()
        );

        IndexResponse response2 = client.index(builder ->
                builder.index("users").document(user));
    }

    @Test
    public void searchData() throws IOException {
        SearchResponse<Account> response = client.search(_0 -> _0
                        .query(_1 -> _1
                                .match(_2 -> _2
                                        .field("address")))
                        .aggregations("ageAgg", _3 -> _3
                                .terms(_4 -> _4
                                        .field("age")
                                        .size(100))
                                .aggregations("balanceAvg", _5 -> _5
                                        .avg(_6 -> _6.field("balance")))),
                Account.class);
        List<Account> documents = response.documents();
        Map<String, Aggregate> aggregations = response.aggregations();
        log.info("documents: {}", documents);
        log.info("aggregations: {}", aggregations);
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
