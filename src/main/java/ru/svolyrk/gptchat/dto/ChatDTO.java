package ru.svolyrk.gptchat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatDTO {

    private Long id;
    private String title;
    private List<MessageDTO> messages;

}
