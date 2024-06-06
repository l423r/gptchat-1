package ru.svolyrk.gptchat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Table("conversation")
public class Conversation {

    @Id
    private Long id;

    @Column("conversation_id")
    private String conversationId;

    @Column("messages")
    private List<String> messages;
}
