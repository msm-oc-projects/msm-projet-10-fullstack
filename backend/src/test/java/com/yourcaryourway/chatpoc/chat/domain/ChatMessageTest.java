package com.yourcaryourway.chatpoc.chat.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatMessageTest {

    @Test
    void trimsValidValues() {
        ChatMessage message = ChatMessage.create(
                UUID.randomUUID(),
                " demo ",
                " Alice ",
                " Bonjour ",
                Instant.parse("2026-06-07T10:00:00Z"));

        assertThat(message.roomId()).isEqualTo("demo");
        assertThat(message.author()).isEqualTo("Alice");
        assertThat(message.content()).isEqualTo("Bonjour");
    }

    @Test
    void rejectsBlankContent() {
        assertThatThrownBy(() -> ChatMessage.create(
                UUID.randomUUID(),
                "demo",
                "Alice",
                "   ",
                Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message is required.");
    }

    @Test
    void rejectsContentOverFiveHundredCharacters() {
        assertThatThrownBy(() -> ChatMessage.create(
                UUID.randomUUID(),
                "demo",
                "Alice",
                "a".repeat(501),
                Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("500");
    }
}
