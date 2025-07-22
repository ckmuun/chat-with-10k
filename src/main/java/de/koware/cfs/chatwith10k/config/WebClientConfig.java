package de.koware.cfs.chatwith10k.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public static final String ACCEPT_ENCODING = "gzip, deflate";

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.sec.gov")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 2048)) // 20 MB
                .defaultHeader("Accept-Encoding", ACCEPT_ENCODING)
                .defaultHeader("User-Agent", "cornelius.koller@online.de")
                .defaultHeader("Accept-Charset", "UTF-8")
                .defaultHeader("Host", "www.sec.gov")
                .build();
    }
}
