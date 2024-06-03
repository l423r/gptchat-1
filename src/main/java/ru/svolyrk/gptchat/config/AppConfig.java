package ru.svolyrk.gptchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestInterceptor loggingRequestInterceptor() {
        return new LoggingRequestInterceptor();
    }
}
