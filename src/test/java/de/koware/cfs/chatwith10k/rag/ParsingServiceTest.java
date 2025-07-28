package de.koware.cfs.chatwith10k.rag;

import de.koware.cfs.chatwith10k.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootTest(classes = ParsingService.class)
class ParsingServiceTest {

    @Autowired
    private ParsingService parsingService;

    @Test
    void stripFormHtml() throws IOException {

        var resource = new ClassPathResource("MMM.html");
        byte[] stripped = parsingService.stripFormHtml(resource.getInputStream());

        Util.writeToDisk(stripped, "MMM-stripped.html");
    }
}