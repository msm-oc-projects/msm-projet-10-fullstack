package com.yourcaryourway.chatpoc.chat.domain;

import java.time.Instant;
import java.util.UUID;

public record ChatMessage(
        UUID id,
        UUID clientMessageId,
        String roomId,
        String author,
        String content,
        Instant sentAt) {

    public static final int MAX_AUTHOR_LENGTH = 40;
    public static final int MAX_CONTENT_LENGTH = 500;

    public ChatMessage {
        if (id == null || clientMessageId == null || sentAt == null) {
            throw new IllegalArgumentException("Message identifiers and timestamp are required.");
        }

        roomId = normalizeRequired(roomId, "Room identifier", 50);
        author = normalizeRequired(author, "Author", MAX_AUTHOR_LENGTH);
        content = normalizeRequired(content, "Message", MAX_CONTENT_LENGTH);

        if (author.length() < 2) {
            throw new IllegalArgumentException("Author must contain at least 2 characters.");
        }
    }

    public static ChatMessage create(
            UUID clientMessageId,
            String roomId,
            String author,
            String content,
            Instant sentAt) {
        return new ChatMessage(UUID.randomUUID(), clientMessageId, roomId, author, content, sentAt);
    }

    private static String normalizeRequired(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }

        String normalized = value.strip();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters.");
        }
        return normalized;
    }
}
