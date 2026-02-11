package com.vinyl.VinylExchange.infrastructure.search.config;

import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchClientConfig {

        @Bean
        public RestHighLevelClient openSearchClient() throws Exception {

                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                // TODO: remove, devonly
                credentialsProvider.setCredentials(AuthScope.ANY,
                                new UsernamePasswordCredentials("admin", "Admin123!DevOnly"));

                SSLContext sslContext = SSLContextBuilder.create()
                                .loadTrustMaterial(null, (chains, authType) -> true)
                                .build();

                RestClientBuilder builder = RestClient
                                .builder(new HttpHost("localhost", 9200, "https"))
                                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                                .setDefaultCredentialsProvider(credentialsProvider)
                                                .setSSLContext(sslContext));

                return new RestHighLevelClient(builder);
        }
}
