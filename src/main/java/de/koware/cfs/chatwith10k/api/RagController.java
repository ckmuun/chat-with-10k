package de.koware.cfs.chatwith10k.api;

import de.koware.cfs.chatwith10k.edgar.CompanyTickerDto;
import de.koware.cfs.chatwith10k.edgar.EdgarService;
import de.koware.cfs.chatwith10k.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class RagController {

    private final EdgarService edgarService;

    public RagController(EdgarService edgarService) {
        this.edgarService = edgarService;
    }

    @GetMapping("latest-10k")
    public Mono<Result<Boolean>> loadLatestTenkForTicker(@RequestParam String ticker) {
        log.debug("Loading latest 10k for ticker: {}", ticker);
        return this.edgarService.loadLatest10KForTicker(ticker);
    }

    @GetMapping("/tickers")
    public Flux<CompanyTickerDto> getTickers() {
        log.debug("Loading tickers");
        return this.edgarService.getTickers();
    }

}
