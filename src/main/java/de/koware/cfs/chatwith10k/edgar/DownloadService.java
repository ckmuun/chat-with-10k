package de.koware.cfs.chatwith10k.edgar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.koware.cfs.chatwith10k.util.Constants.*;

@Service
@Slf4j
public class DownloadService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Scope rootScope = Scope.newEmptyScope();

    private static final String ONE = "1";
    public DownloadService(WebClient webClient) {
        this.webClient = webClient;
        BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, rootScope);
        rootScope.setModuleLoader(BuiltinModuleLoader.getInstance());
    }

    public Flux<CompanyTickerDto> getCompanyTickers() {
        return webClient.get()
                .uri(SEC_BASE + TICKER_FILE_PATH)
                .retrieve()
                .bodyToMono(String.class)
                .flatMapIterable(this::parseCompanyTickerDtos);
    }

    public Flux<CompanyFilingMetadataDto> getCompanyFilings(String cik) {
        cik = addLeadingToZeroesToCik(cik);
        return webClient.get()
                .uri(SEC_BASE_DATA + "/submissions/CIK{cik}.json", cik)
                .retrieve()
                .bodyToMono(String.class)
                .flatMapIterable(this::parseFilings);
    }

    public Mono<CompanyFilingDto> getCompanyFiling(CompanyFilingMetadataDto metadata) {
        var cik = removeLeadingZeroesFromCik(metadata.cik());
        var accessionNumber = metadata.accessionNumber().replace("-", "");
        var filename = metadata.primaryDocument();
        return webClient.get()
                .uri(SEC_BASE + "/Archives/edgar/data/{cik}/{accessionNumber}/{filename}", cik, accessionNumber, filename)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .reduce(DataBuffer::write)
                .map(dataBuffer -> new CompanyFilingDto(metadata, dataBuffer.asInputStream()));
    }

    private String removeLeadingZeroesFromCik(String cik) {
        return cik.replaceFirst("^0+(?!$)", "");
    }

    private String addLeadingToZeroesToCik(String cik) {

        StringBuilder cikBuilder = new StringBuilder(cik);
        while (cikBuilder.length() < 10) {
            cikBuilder.insert(0, "0");
        }
        return cikBuilder.toString();
    }

    protected List<CompanyFilingMetadataDto> parseFilings(String rawResponse) {

        try {
            var prefix = ".filings.recent";
            Scope childScope = Scope.newChildScope(rootScope);
            JsonNode edgarResponse = objectMapper.readTree(rawResponse);
            childScope.setValue("response", edgarResponse);

            // top-level items
            var cik = runQuery(childScope, edgarResponse, ".cik").getFirst().asText();
            var name = runQuery(childScope, edgarResponse, ".name").getFirst().asText();

            // CSV-Style array items
            var accessionNumbers = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".accessionNumber[]"));
            var filingDates = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".filingDate[]"));
            var reportDates = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".reportDate[]"));
            var acceptanceDateTimes = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".acceptanceDateTime[]"));
            var acts = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".act[]"));
            var forms = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".form[]"));
            var fileNumbers = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".fileNumber[]"));
            var filmNumbers = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".filmNumber[]"));
            var items = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".items[]"));
            var coreTypes = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".core_type[]"));
            var sizes = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".size[]"));
            var isXbrls = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".isXBRL[]"));
            var isInlineXbrls = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".isInlineXBRL[]"));
            var primaryDocuments = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".primaryDocument[]"));
            var primaryDocumentDescriptions = parseJsonNodeArray(runQuery(childScope, edgarResponse, prefix + ".primaryDocDescription[]"));

            int length = accessionNumbers.size();

            ArrayList<CompanyFilingMetadataDto> filings = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                filings.add(new CompanyFilingMetadataDto(
                        cik,
                        name,
                        accessionNumbers.get(i),
                        filingDates.get(i),
                        reportDates.get(i),
                        acceptanceDateTimes.get(i),
                        acts.get(i),
                        forms.get(i),
                        fileNumbers.get(i),
                        filmNumbers.get(i),
                        items.get(i),
                        coreTypes.get(i),
                        sizes.get(i),
                        isXbrls.get(i).equals(ONE),
                        isInlineXbrls.get(i).equals(ONE),
                        primaryDocuments.get(i),
                        primaryDocumentDescriptions.get(i)
                ));
            }
            return filings;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private static List<JsonNode> runQuery(Scope childScope, JsonNode edgarResponse, String query) throws JsonQueryException {
        var cikQuery = JsonQuery.compile(query, Versions.JQ_1_6);
        final List<JsonNode> out = new ArrayList<>();
        cikQuery.apply(childScope, edgarResponse, out::add);
        return out;
    }

    private List<String> parseJsonNodeArray(List<JsonNode> nodes) {
        return nodes.stream().map(JsonNode::asText).collect(Collectors.toList());
    }


    protected List<CompanyTickerDto> parseCompanyTickerDtos(String rawResponse) {
        JsonQuery getDataQuery;
        try {
            Scope childScope = Scope.newChildScope(rootScope);
            JsonNode edgarResponse = objectMapper.readTree(rawResponse);
            childScope.setValue("response", edgarResponse);

            getDataQuery = JsonQuery.compile(".data[]", Versions.JQ_1_6);
            final List<JsonNode> out = new ArrayList<>();
            getDataQuery.apply(childScope, edgarResponse, out::add);

            return out.stream().map(
                    this::parseCompanyTickerDto
            ).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        The tickers come as valid, but rather strangely formatted JSON.
        All fields are in an array, in a certain order, but without any
        key-value semantics. The top-level JSON has one child element which
        describes the array formatting.
        Basically, this is CSV semantics in JSON syntax.
     */
    private CompanyTickerDto parseCompanyTickerDto(JsonNode node) {
        var arraynode = (ArrayNode) node;
        var iter = arraynode.elements();
        var cik = iter.next().asText();
        var name = iter.next().asText();
        var ticker = iter.next().asText();
        var exchange = iter.next().asText();

        return new CompanyTickerDto(cik, name, ticker, exchange);
    }
}
