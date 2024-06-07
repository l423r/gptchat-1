package ru.svolyrk.gptchat.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.svolyrk.gptchat.entity.Conversation;
import ru.svolyrk.gptchat.grpc.ChatRequest;
import ru.svolyrk.gptchat.grpc.ChatResponse;
import ru.svolyrk.gptchat.repository.ConversationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private Gpt4Service gpt4Service;

    @Test
    public void testHandleChat() {
        // Given
        String conversationId = "test-conversation";
        String message = "Hello";
        ChatRequest request = ChatRequest.newBuilder()
                .setConversationId(conversationId)
                .setMessage(message)
                .build();
        List<Map<String, String>> context = new ArrayList<>();
        when(conversationRepository.findByConversationId(conversationId))
                .thenReturn(Mono.just(new Conversation()));
        when(gpt4Service.getGpt4Response(anyList()))
                .thenReturn(Mono.just("Hello, how can I help you?"));

        // When
        Mono<ChatResponse> responseMono = chatService.handleChat(request);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getResponse().equals("Hello, how can I help you?"))
                .verifyComplete();
    }

    @Test
    public void testSaveConversation() {
        // Given
        String conversationId = "test-conversation";
        List<Map<String, String>> context = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Hello");
        context.add(message);
        when(conversationRepository.findByConversationId(conversationId))
                .thenReturn(Mono.just(new Conversation()));
        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(Mono.just(new Conversation()));

        // When
        Mono<Void> saveMono = chatService.saveConversation(conversationId, context);

        // Then
        StepVerifier.create(saveMono)
                .verifyComplete();
    }
}
