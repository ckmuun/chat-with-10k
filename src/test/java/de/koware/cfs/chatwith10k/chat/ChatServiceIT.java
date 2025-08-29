package de.koware.cfs.chatwith10k.chat;

import de.koware.cfs.chatwith10k.rag.DocumentService;
import de.koware.cfs.chatwith10k.util.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static de.koware.cfs.chatwith10k.util.TestConstants.SIMPLE_FOUNDING_YEAR_QUESTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Slf4j
public class ChatServiceIT {


    @Autowired
    @SuppressWarnings("unused")
    VectorStore vectorStore;

    @Autowired
    DocumentService documentService;

    @Autowired
    ChatService chatService;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg17");

    @BeforeAll
    static void setup() {
        postgres.start();
    }

    @AfterAll
    static void teardown() {
        postgres.stop();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void send_message_10k_rag_simple_chunking() {
        // Add the short document to the vector store.
        // It's chunked before being embedded and persisted.
        var doc = new Document(TestConstants.MMM_FORM_1A_BUSINESS_SHORT);
        documentService.addDocument(doc);

        var answer = chatService.ragMessage(SIMPLE_FOUNDING_YEAR_QUESTION)
                .reduce((a, b) -> a + b)
                .block();

        assertNotNull(answer);
        assertFalse(answer.isEmpty());

        log.info("answer: {}", answer);
        assertTrue(answer.contains("Delaware"));
        assertTrue(answer.contains("1929"));
    }
}
