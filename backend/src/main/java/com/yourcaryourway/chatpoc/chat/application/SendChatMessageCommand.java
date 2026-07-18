package com.yourcaryourway.chatpoc.chat.application;

import java.util.UUID;

public record SendChatMessageCommand(
        UUID clientMessageId,
        String roomId,
        String author,
        String content) {
}
