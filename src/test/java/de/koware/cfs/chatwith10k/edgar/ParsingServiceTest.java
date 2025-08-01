package de.koware.cfs.chatwith10k.edgar;

import de.koware.cfs.chatwith10k.util.Constants;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ParsingService.class)
class ParsingServiceTest {

    @Autowired
    private ParsingService parsingService;


    @Test
    void formItemExtraction_basic() throws IOException {
        var resource = new ClassPathResource("MMM.html");

        List<Document> documents = parsingService.convertEdgarFormToSpringAiDocuments(
                new CompanyFilingDto(
                        CompanyFilingMetadataDto.builder().form("10-K").build(),
                        resource.getInputStream()
                )
        );

        assertThat(documents).isNotNull();
        assertThat(documents.size()).isGreaterThan(0);
        assertThat(documents.size()).isEqualTo(22);
    }


    @Test
    void testRegex() {
        var pattern = Pattern.compile(Constants.FORM_10K_ITEMS_REGEX);

        assertThat(pattern.matcher("Item 2. Properties").matches()).isTrue();
        assertThat(pattern.matcher("Item 1A. Risk Factors").matches()).isTrue();
        assertThat(pattern.matcher("Item 1A. Risk-Factors").matches()).isTrue();
        assertThat(pattern.matcher("Item 6. [Reserved]").matches()).isTrue();
        assertThat(pattern.matcher("Item 7. Management’s Discussion and Analysis of Financial Condition and Results of Operations").matches()).isTrue();
        assertThat(pattern.matcher("Item 1C. Cybersecurity").matches()).isTrue();
        assertThat(pattern.matcher("Item 1A Risk Factors").matches()).isTrue();
        assertThat(pattern.matcher("Item 9B. Other Information").matches()).isTrue();
        assertThat(pattern.matcher("Item 10. Directors, Executive Officers and Corporate Governance").matches()).isTrue();
        assertThat(pattern.matcher("Item 11. Executive Compensation").matches()).isTrue();
        assertThat(pattern.matcher("Item 5. Market for Registrant’s, Common Equity, Related Stockholder Matters and Issuer Purchases of Equity Securities").matches()).isTrue();
        assertThat(pattern.matcher("Item 16 Something").matches()).isTrue();
    }

    @Test
    void stripFormHtml() throws IOException {

        var resource = new ClassPathResource("MMM.html");
        byte[] stripped = parsingService.stripFormHtml(Jsoup.parse(resource.getFile())).html().getBytes();

        assertThat(stripped).isNotNull();
        assertThat(stripped.length).isGreaterThan(0);
//        Util.writeToDisk(stripped, "MMM-stripped.html");
    }

    @Test
    void extractXbrlHeader() throws IOException {
        var resource = new ClassPathResource("MMM.html");
        byte[] xbrlHeader = parsingService.getXbrlHeader(Jsoup.parse(resource.getFile()));
        assertThat(xbrlHeader).isNotNull();
        assertThat(xbrlHeader.length).isGreaterThan(0);
//        Util.writeToDisk(xbrlHeader, "MMM-xbrl.html");
    }

}