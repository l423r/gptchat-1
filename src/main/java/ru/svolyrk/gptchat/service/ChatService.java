package ru.svolyrk.gptchat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.svolyrk.gptchat.entity.Conversation;
import ru.svolyrk.gptchat.grpc.ChatResponse;
import ru.svolyrk.gptchat.repository.ConversationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private Gpt4Service gpt4Service;

    @Autowired
    private ConversationRepository conversationRepository;

    public Mono<ChatResponse> handleChat(ru.svolyrk.gptchat.grpc.ChatRequest request) {
        String conversationId = request.getConversationId();
        String message = request.getMessage();

        return getConversation(conversationId).flatMap(context -> {
            // Добавляем сообщение пользователя в контекст
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            context.add(userMessage);

            // Вызываем GPT-4 API для получения ответа
            return gpt4Service.getGpt4Response(context).flatMap(response -> {
                // Добавляем ответ GPT в контекст
                Map<String, String> gptResponse = new HashMap<>();
                gptResponse.put("role", "assistant");
                gptResponse.put("content", response);
                context.add(gptResponse);

                // Сохраняем контекст диалога в базе данных
                return saveConversation(conversationId, context).thenReturn(ChatResponse.newBuilder()
                        .setConversationId(conversationId)
                        .setResponse(response)
                        .build());
            });
        });
    }

    public Mono<Void> saveConversation(String conversationId, List<Map<String, String>> context) {
        return conversationRepository.findByConversationId(conversationId)
                .defaultIfEmpty(new Conversation())
                .flatMap(conversation -> {
                    conversation.setConversationId(conversationId);

                    List<String> messages = new ArrayList<>();
                    for (Map<String, String> message : context) {
                        messages.add(message.get("role") + ": " + message.get("content"));
                    }
                    conversation.setMessages(messages);

                    return conversationRepository.save(conversation).then();
                });
    }

    public Mono<List<Map<String, String>>> getConversation(String conversationId) {
        return conversationRepository.findByConversationId(conversationId)
                .map(conversation -> {
                    List<Map<String, String>> context = new ArrayList<>();
                    for (String message : conversation.getMessages()) {
                        String[] parts = message.split(": ", 2);
                        if (parts.length == 2) {
                            Map<String, String> messageMap = new HashMap<>();
                            messageMap.put("role", parts[0]);
                            messageMap.put("content", parts[1]);
                            context.add(messageMap);
                        }
                    }
                    return context;
                })
                .defaultIfEmpty(new ArrayList<>());
    }
}
