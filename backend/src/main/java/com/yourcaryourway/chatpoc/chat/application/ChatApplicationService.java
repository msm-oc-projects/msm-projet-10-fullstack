package com.yourcaryourway.chatpoc.chat.application;

import com.yourcaryourway.chatpoc.chat.domain.ChatMessage;
import com.yourcaryourway.chatpoc.chat.domain.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
public class ChatApplicationService {

    private static final int MAX_HISTORY_LIMIT = 100;

    private final ChatMessageRepository repository;
    private final Clock clock;

    @Autowired
    public ChatApplicationService(ChatMessageRepository repository) {
        this(repository, Clock.systemUTC());
    }

    ChatApplicationService(ChatMessageRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public ChatMessage send(SendChatMessageCommand command) {
        if (command == null || command.clientMessageId() == null) {
            throw new IllegalArgumentException("Client message identifier is required.");
        }

        return repository.findByClientMessageId(command.clientMessageId())
                .orElseGet(() -> repository.save(ChatMessage.create(
                        command.clientMessageId(),
                        command.roomId(),
                        command.author(),
                        command.content(),
                        clock.instant())));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> history(String roomId, int requestedLimit) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room identifier is required.");
        }
        int limit = Math.max(1, Math.min(requestedLimit, MAX_HISTORY_LIMIT));
        return repository.findRecentByRoomId(roomId.strip(), limit);
    }
}
