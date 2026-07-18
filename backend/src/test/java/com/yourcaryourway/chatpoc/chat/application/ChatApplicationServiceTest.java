package com.yourcaryourway.chatpoc.chat.application;

import com.yourcaryourway.chatpoc.chat.domain.ChatMessage;
import com.yourcaryourway.chatpoc.chat.domain.ChatMessageRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatApplicationServiceTest {

    private final InMemoryRepository repository = new InMemoryRepository();
    private final ChatApplicationService service = new ChatApplicationService(
            repository,
            Clock.fixed(Instant.parse("2026-06-07T10:00:00Z"), ZoneOffset.UTC));

    @Test
    void storesANewMessage() {
        UUID clientMessageId = UUID.randomUUID();

        ChatMessage message = service.send(
                new SendChatMessageCommand(clientMessageId, "demo", "Alice", "Bonjour"));

        assertThat(message.clientMessageId()).isEqualTo(clientMessageId);
        assertThat(message.sentAt()).isEqualTo(Instant.parse("2026-06-07T10:00:00Z"));
        assertThat(repository.messages).hasSize(1);
    }

    @Test
    void returnsExistingMessageForTheSameClientIdentifier() {
        UUID clientMessageId = UUID.randomUUID();
        SendChatMessageCommand command =
                new SendChatMessageCommand(clientMessageId, "demo", "Alice", "Bonjour");

        ChatMessage first = service.send(command);
        ChatMessage repeated = service.send(command);

        assertThat(repeated).isEqualTo(first);
        assertThat(repository.messages).hasSize(1);
    }

    @Test
    void capsHistoryAtOneHundredMessages() {
        for (int index = 0; index < 120; index++) {
            repository.save(ChatMessage.create(
                    UUID.randomUUID(),
                    "demo",
                    "Alice",
                    "Message " + index,
                    Instant.parse("2026-06-07T10:00:00Z").plusSeconds(index)));
        }

        assertThat(service.history("demo", 500)).hasSize(100);
    }

    private static final class InMemoryRepository implements ChatMessageRepository {
        private final List<ChatMessage> messages = new ArrayList<>();

        @Override
        public Optional<ChatMessage> findByClientMessageId(UUID clientMessageId) {
            return messages.stream()
                    .filter(message -> message.clientMessageId().equals(clientMessageId))
                    .findFirst();
        }

        @Override
        public ChatMessage save(ChatMessage message) {
            messages.add(message);
            return message;
        }

        @Override
        public List<ChatMessage> findRecentByRoomId(String roomId, int limit) {
            return messages.stream()
                    .filter(message -> message.roomId().equals(roomId))
                    .sorted(Comparator.comparing(ChatMessage::sentAt).reversed())
                    .limit(limit)
                    .sorted(Comparator.comparing(ChatMessage::sentAt))
                    .toList();
        }
    }
}
