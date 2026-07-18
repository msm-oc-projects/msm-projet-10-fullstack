package com.yourcaryourway.chatpoc.chat.infrastructure.web;

import com.yourcaryourway.chatpoc.chat.application.ChatApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/rooms")
class ChatRestController {

    private final ChatApplicationService service;

    ChatRestController(ChatApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{roomId}/messages")
    List<ChatMessageResponse> history(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit) {
        return service.history(roomId, limit).stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
}
