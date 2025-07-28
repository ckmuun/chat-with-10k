package de.koware.cfs.chatwith10k.rag;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class TenKFormReader {

    /*
        Reads 10K (file) input stream and creates Spring AI document.
        Main difference is automatic partitioning of documents.
     */
    public Document read10kDocument(InputStream TenKInputStream) {

        return null; // todo
    }
}
