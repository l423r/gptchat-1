package ru.svolyrk.gptchat.repository;

import ru.svolyrk.gptchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
