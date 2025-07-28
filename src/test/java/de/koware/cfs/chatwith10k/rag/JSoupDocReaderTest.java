package de.koware.cfs.chatwith10k.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JSoupDocReaderTest {

    @Test
    void testDocumentReading_basic() {
        var resource = new ClassPathResource("AAPL.txt");

        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("SEC-DOCUMENT p") // Extract paragraphs within <article> tags
                .charset("ISO-8859-1")  // Use ISO-8859-1 encoding
                .includeLinkUrls(true) // Include link URLs in metadata
                .additionalMetadata("source", "my-page.html") // Add custom metadata
                .build();

        JsoupDocumentReader reader = new JsoupDocumentReader(resource, config);
        var docs = reader.get();

        assertNotNull(docs);
        assertFalse(docs.isEmpty());
    }
}