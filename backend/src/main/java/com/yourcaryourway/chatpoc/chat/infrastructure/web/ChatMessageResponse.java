package com.yourcaryourway.chatpoc.chat.infrastructure.web;

import com.yourcaryourway.chatpoc.chat.domain.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID clientMessageId,
        String roomId,
        String author,
        String content,
        Instant sentAt) {

    static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.id(),
                message.clientMessageId(),
                message.roomId(),
                message.author(),
                message.content(),
                message.sentAt());
    }
}
