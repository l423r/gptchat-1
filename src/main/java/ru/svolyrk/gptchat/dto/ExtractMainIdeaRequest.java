package ru.svolyrk.gptchat.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ExtractMainIdeaRequest {

    @NotBlank
    private String text;
}
