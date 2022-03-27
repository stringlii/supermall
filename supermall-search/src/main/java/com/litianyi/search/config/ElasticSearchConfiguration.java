package com.litianyi.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/27 5:02 PM
 */
@Configuration
@EnableConfigurationProperties(ElasticSearchProperties.class)
public class ElasticSearchConfiguration {
    private final RestHighLevelClient restHighLevelClient;

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        /*builder.addHeader("Authorization", "Bearer " + TOKEN);
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));*/
        COMMON_OPTIONS = builder.build();
    }

    public ElasticSearchConfiguration(ElasticSearchProperties properties) {
        //初始化ES操作客户端
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));

        this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(properties.getHostname(), properties.getPort()))
                        .setHttpClientConfigCallback(httpClientBuilder -> {
                            httpClientBuilder.disableAuthCaching();
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }));
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return this.restHighLevelClient;
    }

    @Bean
    public ElasticsearchClient restClient() {
        // Create the new Java Client with the same low level client
        ElasticsearchTransport transport = new RestClientTransport(
                this.restHighLevelClient.getLowLevelClient(),
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }
}
