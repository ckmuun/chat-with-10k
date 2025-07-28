package de.koware.cfs.chatwith10k.edgar;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;


    @Bean
    @Qualifier("embeddingModel")
    public EmbeddingModel embeddingModel() {
        return ollamaEmbeddingModel;
    }
}
