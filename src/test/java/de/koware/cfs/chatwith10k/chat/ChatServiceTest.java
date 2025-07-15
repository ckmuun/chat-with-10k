package de.koware.cfs.chatwith10k.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Test
    void sendMessage_basic_addition() {
        var question = "What is 2+2?";
        var answer = chatService
                .sendMessage(question)
                .reduce((a, b) -> a + b)
                .block();

        assertNotNull(answer);
        assertFalse(answer.isEmpty());
        assertTrue(answer.contains("4"));
    }
}