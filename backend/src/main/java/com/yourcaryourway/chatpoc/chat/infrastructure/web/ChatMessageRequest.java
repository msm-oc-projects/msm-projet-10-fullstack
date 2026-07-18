package com.yourcaryourway.chatpoc.chat.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChatMessageRequest(
        @NotNull UUID clientMessageId,
        @NotBlank @Size(min = 2, max = 40) String author,
        @NotBlank @Size(max = 500) String content) {
}
