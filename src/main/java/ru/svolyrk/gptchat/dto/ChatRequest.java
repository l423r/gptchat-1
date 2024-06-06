package ru.svolyrk.gptchat.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {

    private String conversationId;

    @NotBlank
    private String message;

    private List<Map<String, String>> context;
}
