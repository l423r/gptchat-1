package ru.svolyrk.gptchat.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class TextToSpeechRequest {

    @NotBlank
    private String text;
}
