package ru.svolyrk.gptchat.config;

import org.hibernate.sql.exec.spi.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution executionContext) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = executionContext.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        log.debug("Request URI: {}", request.getURI());
        log.debug("Request Method: {}", request.getMethod());
        log.debug("Request Headers: {}", request.getHeaders());
        log.debug("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
        }
        log.debug("Response Status Code: {}", response.getStatusCode());
        log.debug("Response Headers: {}", response.getHeaders());
        log.debug("Response Body: {}", inputStringBuilder.toString());
    }

}
