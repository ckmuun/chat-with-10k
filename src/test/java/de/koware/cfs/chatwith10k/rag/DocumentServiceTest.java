package de.koware.cfs.chatwith10k.rag;

import de.koware.cfs.chatwith10k.util.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.ollama.autoconfigure.OllamaEmbeddingAutoConfiguration;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DocumentService.class, RagConfig.class, OllamaEmbeddingAutoConfiguration.class, EmbeddingModel.class})
@Slf4j
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmbeddingModel embeddingModel;

    @MockitoBean
    private VectorStore vectorStore;

    @Test
    void testDocumentSplitting_basic() {

        var document = new Document(TestConstants.MMM_FORM_1A_BUSINESS);

        var chunks = documentService.chunkDocument(document);
        assertFalse(chunks.isEmpty());
        assertFalse(chunks.size() <= 2);

        for (Document chunk: chunks) {
            float[] embedding = embeddingModel.embed(chunk);
            log.debug("Embedding: {}", Arrays.toString(embedding));
        }
    }


}