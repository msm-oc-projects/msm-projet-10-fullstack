package com.yourcaryourway.chatpoc.chat.infrastructure.web;

import com.yourcaryourway.chatpoc.chat.application.ChatApplicationService;
import com.yourcaryourway.chatpoc.chat.application.SendChatMessageCommand;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
class ChatWebSocketController {

    private final ChatApplicationService service;
    private final SimpMessagingTemplate messagingTemplate;

    ChatWebSocketController(
            ChatApplicationService service,
            SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{roomId}/send")
    void send(
            @DestinationVariable String roomId,
            ChatMessageRequest request) {
        try {
            ChatMessageResponse response = ChatMessageResponse.from(service.send(
                    new SendChatMessageCommand(
                            request.clientMessageId(),
                            roomId,
                            request.author(),
                            request.content())));
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
        } catch (IllegalArgumentException exception) {
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + roomId + "/errors",
                    new ChatErrorResponse(exception.getMessage()));
        }
    }

    record ChatErrorResponse(String message) {
    }
}
