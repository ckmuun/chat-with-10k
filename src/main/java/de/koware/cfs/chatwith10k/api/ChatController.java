package de.koware.cfs.chatwith10k.api;

import de.koware.cfs.chatwith10k.chat.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send-message")
    public Flux<String> sendMessage(@RequestBody String message) {
        log.debug("Received message for LLM: {}", message);
        return chatService.sendMessage(message);
    }

    @GetMapping("/hello")
    public Mono<String> getGreeting() {
        return Mono.just("Hello World!");
    }

}
