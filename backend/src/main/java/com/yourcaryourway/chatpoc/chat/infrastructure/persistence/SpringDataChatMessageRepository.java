package com.yourcaryourway.chatpoc.chat.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

    Optional<ChatMessageEntity> findByClientMessageId(UUID clientMessageId);

    List<ChatMessageEntity> findByRoomIdOrderBySentAtDesc(String roomId, Pageable pageable);
}
