package com.yourcaryourway.chatpoc.chat.infrastructure.persistence;

import com.yourcaryourway.chatpoc.chat.domain.ChatMessage;
import com.yourcaryourway.chatpoc.chat.domain.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaChatMessageRepository implements ChatMessageRepository {

    private final SpringDataChatMessageRepository repository;

    JpaChatMessageRepository(SpringDataChatMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ChatMessage> findByClientMessageId(UUID clientMessageId) {
        return repository.findByClientMessageId(clientMessageId).map(this::toDomain);
    }

    @Override
    public ChatMessage save(ChatMessage message) {
        ChatMessageEntity entity = new ChatMessageEntity(
                message.id(),
                message.clientMessageId(),
                message.roomId(),
                message.author(),
                message.content(),
                message.sentAt());
        return toDomain(repository.save(entity));
    }

    @Override
    public List<ChatMessage> findRecentByRoomId(String roomId, int limit) {
        List<ChatMessage> descending = repository
                .findByRoomIdOrderBySentAtDesc(roomId, PageRequest.of(0, limit))
                .stream()
                .map(this::toDomain)
                .toList();
        return descending.reversed();
    }

    private ChatMessage toDomain(ChatMessageEntity entity) {
        return new ChatMessage(
                entity.getId(),
                entity.getClientMessageId(),
                entity.getRoomId(),
                entity.getAuthor(),
                entity.getContent(),
                entity.getSentAt());
    }
}
