package de.koware.cfs.chatwith10k.edgar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentService {


    private final VectorStore vectorStore;

    @Autowired
    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    public void addDocument(Document document) {
        vectorStore.add(List.of(document));
    }


}
