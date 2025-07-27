package de.koware.cfs.chatwith10k.edgar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.koware.cfs.chatwith10k.config.Constants;
import lombok.extern.slf4j.Slf4j;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TenKDownloadService {


    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Scope rootScope = Scope.newEmptyScope();


    public TenKDownloadService(WebClient webClient) {
        this.webClient = webClient;
        BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, rootScope);
        rootScope.setModuleLoader(BuiltinModuleLoader.getInstance());
    }

    public Mono<List<CompanyTickerDto>> getCompanyTickers() {
        return webClient.get()
                .uri(Constants.TICKER_FILE_PATH)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseCompanyTickerDtos);
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
