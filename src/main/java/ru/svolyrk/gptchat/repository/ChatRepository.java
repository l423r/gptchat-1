package ru.svolyrk.gptchat.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.svolyrk.gptchat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @EntityGraph(attributePaths = {"messages"})
    Optional<Chat> findById(Long id);
}
