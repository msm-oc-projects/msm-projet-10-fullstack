package com.yourcaryourway.chatpoc.chat.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository {

    Optional<ChatMessage> findByClientMessageId(UUID clientMessageId);

    ChatMessage save(ChatMessage message);

    List<ChatMessage> findRecentByRoomId(String roomId, int limit);
}
