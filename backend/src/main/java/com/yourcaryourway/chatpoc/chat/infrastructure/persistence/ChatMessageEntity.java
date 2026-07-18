package com.yourcaryourway.chatpoc.chat.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
class ChatMessageEntity {

    @Id
    private UUID id;

    @Column(name = "client_message_id", nullable = false, unique = true)
    private UUID clientMessageId;

    @Column(name = "room_id", nullable = false, length = 50)
    private String roomId;

    @Column(nullable = false, length = 40)
    private String author;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected ChatMessageEntity() {
    }

    ChatMessageEntity(
            UUID id,
            UUID clientMessageId,
            String roomId,
            String author,
            String content,
            Instant sentAt) {
        this.id = id;
        this.clientMessageId = clientMessageId;
        this.roomId = roomId;
        this.author = author;
        this.content = content;
        this.sentAt = sentAt;
    }

    UUID getId() {
        return id;
    }

    UUID getClientMessageId() {
        return clientMessageId;
    }

    String getRoomId() {
        return roomId;
    }

    String getAuthor() {
        return author;
    }

    String getContent() {
        return content;
    }

    Instant getSentAt() {
        return sentAt;
    }
}
