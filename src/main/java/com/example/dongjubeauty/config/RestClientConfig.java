package com.example.dongjubeauty.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PythonApiProperties.class)
@RequiredArgsConstructor
public class RestClientConfig {

    private final PythonApiProperties props;

    @Bean
    public RestClient pythonRestClient(RestClient.Builder builder) {
        // 가벼운 JDK HttpURLConnection 기반 + 타임아웃 설정
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(3_000); // 3s
        rf.setReadTimeout(20_000);   // 20s

        return builder
                .baseUrl(props.getBaseUrl())
                .requestFactory(rf)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
