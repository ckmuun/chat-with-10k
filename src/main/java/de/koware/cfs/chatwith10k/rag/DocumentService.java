package de.koware.cfs.chatwith10k.rag;

import de.koware.cfs.chatwith10k.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentService {


    private final VectorStore vectorStore;
    private final TextSplitter textSplitter;

    @Autowired
    public DocumentService(VectorStore vectorStore, TextSplitter textSplitter) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }

    public Result<Boolean> addDocuments(List<Document> documents) {
        try {
            documents.forEach(
                    this::addDocument
            );
            return new Result<>(true);
        } catch (Exception e) {
            return new Result<>(false);
        }
    }

    /*
        This is exposed as its own method for testing purposes.
     */
    protected List<Document> chunkDocument(Document document) {
        log.debug("splitting document into chunks");
        return textSplitter.apply(List.of(document));
    }

    public void addDocument(Document document) {
        var chunks = chunkDocument(document);
        vectorStore.add(chunks);
    }
}
