package de.koware.cfs.chatwith10k.edgar;

import de.koware.cfs.chatwith10k.util.Constants;
import de.koware.cfs.chatwith10k.rag.DocumentService;
import de.koware.cfs.chatwith10k.util.Result;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EdgarService {

    private final DownloadService downloadService;
    private final ParsingService parsingService;
    private final DocumentService documentService;

    public EdgarService(DownloadService downloadService, ParsingService parsingService, DocumentService documentService) {
        this.downloadService = downloadService;
        this.parsingService = parsingService;
        this.documentService = documentService;
    }

    public Mono<Result<Boolean>> loadLatest10KForTicker(String ticker) {
        return downloadService
                .getCompanyTickers()
                .filter(dto -> dto.ticker().equals(ticker))
                .flatMap(dto ->
                        downloadService.getCompanyFilings(dto.cik()))
                .filter(filingDto -> filingDto.form().equals(Constants.TEN_K_FORM))
                .flatMap(downloadService::getCompanyFiling)
                .last()
                .map(parsingService::convertEdgarFormToSpringAiDocuments)
                .map(documentService::addDocuments);
    }

    public Flux<CompanyTickerDto> getTickers() {
        return downloadService.getCompanyTickers();
    }
}
