package de.koware.cfs.chatwith10k.chat;

import de.koware.cfs.chatwith10k.util.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @MockitoBean
    private VectorStore vectorStore;

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

    @Test
    void sendMessage_rag_basic() {
        // Make sure the document content is not contained in the model's training data.
        var doc = new Document("The potato has a red and blue tartan pattern");

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(
                List.of(doc)
        );

        var answer = chatService.ragMessage("Which pattern does the potato have?")
                .reduce((a, b) -> a + b)
                .block();

        assertNotNull(answer);
        assertFalse(answer.isEmpty());
        assertTrue(answer.contains("red and blue tartan pattern"));
    }

    @Test
    void sendMessage_rag_10K_simple() {
        // Make sure the document content is not contained in the model's training data.
        var doc = new Document(TestConstants.MMM_FORM_1A_BUSINESS_SHORT);

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(
                List.of(doc)
        );

        var answer = chatService.ragMessage("When and where was the 3M company founded?")
                .reduce((a, b) -> a + b)
                .block();

        assertNotNull(answer);
        assertFalse(answer.isEmpty());

        log.info("answer: {}", answer);
        assertTrue(answer.contains("Delaware"));
        assertTrue(answer.contains("1929"));
    }
}