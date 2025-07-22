package de.koware.cfs.chatwith10k.edgar;

import de.koware.cfs.chatwith10k.config.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(classes = {TenKDownloadService.class, WebClientConfig.class})
@Slf4j
class TenKDownloadServiceTest {

    @Autowired
    private TenKDownloadService downloadService;

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
}