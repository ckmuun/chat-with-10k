package de.koware.cfs.chatwith10k.rag;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RagConfig {

    @Bean
    public TextSplitter tokenTextSplitter() {
        return new TokenTextSplitter(
                100,
                15,
                5,
                25000,
                true
        );
    }

    @Bean
    @Profile("semantic-chunking")
    public TextSplitter textSplitter() {
        return new SemanticTextSplitter();
    }
}