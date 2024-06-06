package ru.svolyrk.gptchat.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.svolyrk.gptchat.entity.Conversation;
import reactor.core.publisher.Mono;

public interface ConversationRepository extends ReactiveCrudRepository<Conversation, Long> {
    Mono<Conversation> findByConversationId(String conversationId);
}
