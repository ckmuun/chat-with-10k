package de.koware.cfs.chatwith10k.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
public class SemanticTextSplitter extends TextSplitter {

        /*
        TODO: First use the regular token-based chunker as a benchmark.
          Then implement this to see if it is actually noticeably better.
     */

    @Override
    protected List<String> splitText(String text) {
        // todo
        return List.of();
    }


    @Override
    public List<Document> transform(List<Document> transform) {
        // todo
        return super.transform(transform);
    }
}
