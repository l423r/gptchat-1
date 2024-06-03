package ru.svolyrk.gptchat.repository;

import ru.svolyrk.gptchat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
