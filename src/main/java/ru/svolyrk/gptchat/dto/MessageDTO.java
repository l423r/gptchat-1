package ru.svolyrk.gptchat.dto;

import lombok.Data;

@Data
public class MessageDTO {

    private Long id;
    private String sender;
    private String content;

    // Getters and setters
}
