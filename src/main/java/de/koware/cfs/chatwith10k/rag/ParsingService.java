package de.koware.cfs.chatwith10k.rag;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ParsingService {


    protected void getXbrlHeader(InputStream htmlInputStream) {
        try {

            var document = Jsoup.parse(
                    htmlInputStream, "UTF-8", ""
            );
            var xbrlItems = document.getElementsByTag("ix:header");

            var text = xbrlItems.html();

            var formItems = document.getElementsByTag("form");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Removes inline styles & colspans, links and empty html tags.
     */
    protected byte[] stripFormHtml(InputStream htmlInputStream) {
        try {
            var document = Jsoup.parse(
                    htmlInputStream, "UTF-8", ""
            );
            // remove inline styles (reduces file size and makes it more readable during development)
            Elements elementsWithStyle = document.select("[style]");
            for (Element element : elementsWithStyle) {
                element.removeAttr("style");
            }
            Elements elementsWithColspan = document.select("[colspan]");
            for (Element element : elementsWithColspan) {
                element.removeAttr("colspan");
            }
            // Select all elements
            Elements allElements = document.getAllElements();

            // Iterate in reverse to avoid modifying the tree while traversing
            for (int i = allElements.size() - 1; i >= 0; i--) {
                Element el = allElements.get(i);
                // Check if element is empty or contains only whitespace
                if (el.children().isEmpty() && el.text().trim().isEmpty()) {
                    el.remove();
                }
            }
            // Select all <a> elements
            Elements links = document.select("a");

            // Remove each <a> element
            for (Element link : links) {
                link.remove();
            }

            return document.html().getBytes(StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
