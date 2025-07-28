package de.koware.cfs.chatwith10k.rag;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
