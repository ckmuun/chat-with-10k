package de.koware.cfs.chatwith10k.edgar;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.koware.cfs.chatwith10k.config.Constants.FORM_10K_ITEMS_REGEX;

@Service
@Slf4j
public class ParsingService {


    /*
        Create a list of Spring AI documents from an Edgar form.
     */
    public List<Document> partitionEdgarForm(CompanyFilingDto companyFilingDto) throws IOException {
        List<Document> documents = new ArrayList<>();

        var htmlDocument = Jsoup.parse(companyFilingDto.file(), "UTF-8", "");

        byte[] xbrl = this.getXbrlHeader(htmlDocument);
        htmlDocument = this.stripFormHtml(htmlDocument);

        // XBRL header
        documents.add(new Document(
                new String(xbrl)
        ));

        // Items of the form. Currently only 10-K supported. TODO add schemas for other edgar forms
        if (!companyFilingDto.metadata().form().equals("10-K")) {
            throw new IllegalArgumentException("Currently only 10-K forms supported");
        }

        var formItems = getFormItemsFromHtml(htmlDocument, Pattern.compile(FORM_10K_ITEMS_REGEX));
        documents.addAll(formItems);
        return documents;
    }

    protected List<Document> getFormItemsFromHtml(org.jsoup.nodes.Document htmlDocument, Pattern separatorRegex) {
        return getFormItemsFromHtml(htmlDocument, separatorRegex, separatorRegex);
    }

    protected List<Document> getFormItemsFromHtml(org.jsoup.nodes.Document htmlDocument, Pattern beginRegex, Pattern endRegex) {
        List<Document> documents = new ArrayList<>();


        boolean match = false;
        var content = new StringBuilder();
        for (Element e : htmlDocument.getAllElements()) {
            if (match && endRegex.matcher(e.ownText()).matches()) {
                documents.add(new Document(content.toString()));
                match = false;
                content = new StringBuilder();
            }
            if (beginRegex.matcher(e.ownText()).matches()) {
                match = true;
            }
            if (match) {
                content.append(' ');
                content.append(e.ownText());
            }
        }
        return documents;
    }


    protected byte[] getXbrlHeader(org.jsoup.nodes.Document htmlDocument) {
        var xbrlItems = htmlDocument.getElementsByTag("ix:header");
        htmlDocument.getElementsByTag("ix:header").remove();
        return xbrlItems.html().getBytes(StandardCharsets.UTF_8);
    }


    /*
        Removes inline styles & colspans, links and empty html tags.
     */
    protected org.jsoup.nodes.Document stripFormHtml(org.jsoup.nodes.Document htmlDocument) {
        // remove inline styles (reduces file size and makes it more readable during development)
        Elements elementsWithStyle = htmlDocument.select("[style]");
        for (Element element : elementsWithStyle) {
            element.removeAttr("style");
        }
        Elements elementsWithColspan = htmlDocument.select("[colspan]");
        for (Element element : elementsWithColspan) {
            element.removeAttr("colspan");
        }
        // Select all elements
        Elements allElements = htmlDocument.getAllElements();

        // Iterate in reverse to avoid modifying the tree while traversing
        for (int i = allElements.size() - 1; i >= 0; i--) {
            Element el = allElements.get(i);
            // Check if element is empty or contains only whitespace
            if (el.children().isEmpty() && el.text().trim().isEmpty()) {
                el.remove();
            }
        }
        // Select all <a> elements
        Elements links = htmlDocument.select("a");

        // Remove each <a> element
        for (Element link : links) {
            link.remove();
        }

        return htmlDocument;
    }
}
