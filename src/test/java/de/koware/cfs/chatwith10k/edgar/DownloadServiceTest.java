package de.koware.cfs.chatwith10k.edgar;

import de.koware.cfs.chatwith10k.config.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {DownloadService.class, WebClientConfig.class})
@Slf4j
class DownloadServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Test
    void getCompanyTickers_basic() {
        var resp = downloadService.getCompanyTickers()
                .block();
        log.info("resp={}", resp);
    }

    @Test
    void parseCompanyTickers() throws IOException {
        var resource = new ClassPathResource("tickers.json");
        var tickers = downloadService.parseCompanyTickerDtos(resource
                .getContentAsString(Charset.defaultCharset()));


        assertThat(tickers).isNotNull();
        assertFalse(tickers.isEmpty());
        assertEquals(52, tickers.size());
    }


    @Test
    void parseFilings_3M() throws IOException {
        var resource = new ClassPathResource("filings.json");

        var filings = downloadService.parseFilings(resource.getContentAsString(StandardCharsets.UTF_8));

        assertThat(filings).isNotNull();
        assertFalse(filings.isEmpty());
        assertEquals(1000, filings.size());

        var firstFiling = filings.getFirst();

        assertThat(firstFiling).isNotNull();
        assertEquals("4", firstFiling.coreType());
    }

    @Test
    void parseFilings_3M_10k() throws IOException {
        var resource = new ClassPathResource("filings.json");

        var filings = downloadService.parseFilings(resource.getContentAsString(StandardCharsets.UTF_8));

        assertThat(filings).isNotNull();
        assertFalse(filings.isEmpty());
        assertEquals(1000, filings.size());

        var tenKOpt = filings.stream().filter(
                f -> f.form().equals("10-K")
        ).findFirst();

        assertThat(tenKOpt).isNotNull();
        assertTrue(tenKOpt.isPresent());

        var tenk = tenKOpt.get();
        assertThat(tenk).isNotNull();
        assertEquals("10-K", tenk.form());
    }

    @Test
    void fetch10KForm_3M() throws IOException {
        var metadata = CompanyFilingMetadataDto.builder()
                .accessionNumber("0000066740-25-000006")
                .primaryDocument("mmm-20241231.htm")
                .cik("0000066740")
                .build();

        var tenKFormFiling = downloadService.getCompanyFiling(metadata).block();
        assertThat(tenKFormFiling).isNotNull();

        assertNotNull(tenKFormFiling);
        byte[] data = tenKFormFiling.file().readAllBytes();
        assertThat(data).isNotEmpty();

        // local development only
//        writeToDisk(data, "3M.html");
    }



    @Test
    void fetchFilings_3M() {

        var cik = "66740";
        var filings = downloadService.getCompanyFilings(cik).block();

        assertNotNull(filings);
        assertEquals(1000, filings.size());

        var firstFiling = filings.getFirst();

        assertThat(firstFiling).isNotNull();
        assertEquals("4", firstFiling.coreType());
    }
}