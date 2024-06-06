package ru.svolyrk.gptchat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Gpt4ServiceTest {

    @InjectMocks
    private Gpt4Service gpt4Service;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Value("${gpt4.api.url}")
    private String apiUrl;

    @Value("${gpt4.api.key}")
    private String apiKey;

    @BeforeEach
    public void setUp() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Gpt4Service.GptResponse.class))
                .thenReturn(Mono.just(new Gpt4Service.GptResponse(List.of(new Gpt4Service.GptResponse.Choice(new Gpt4Service.GptResponse.Message("Hello, how can I help you?"))))));
        when(webClient.post()).thenReturn(mock(WebClient.RequestBodyUriSpec.class));
        when(webClientBuilder.baseUrl(apiUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    public void testGetGpt4Response() {
        // Given
        List<Map<String, String>> context = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Hello");
        context.add(message);

        // When
        Mono<String> responseMono = gpt4Service.getGpt4Response(context);

        // Then
        StepVerifier.create(responseMono)
                .expectNext("Hello, how can I help you?")
                .verifyComplete();
    }
}
