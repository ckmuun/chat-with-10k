package de.koware.cfs.chatwith10k.api;

import de.koware.cfs.chatwith10k.chat.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestBody String message) {
        log.debug("Received message for LLM: {}", message);
        return chatService.sendMessage(message);
    }

}
