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

    public TenKDownloadService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<CompanyTickerDto>> getCompanyTickers() {
        return webClient.get()
                .uri("/files/company_tickers_exchange.json")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseCompanyTickerDtos);
    }

    protected List<CompanyTickerDto> parseCompanyTickerDtos(String rawResponse) {
        // todo put this into some static/singleton bean
        ObjectMapper objectMapper = new ObjectMapper();
        Scope rootScope = Scope.newEmptyScope();
        BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, rootScope);
        rootScope.setModuleLoader(BuiltinModuleLoader.getInstance());

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

    private CompanyTickerDto parseCompanyTickerDto(JsonNode node) {
        var arraynode = (ArrayNode) node;
        var iter = arraynode.elements();
        var cik = iter.next().asText();
        var name = iter.next().asText();
        var ticker = iter.next().asText();
        var exchange = iter.next().asText();

        return  new CompanyTickerDto(cik, name, ticker, exchange);
    }
}
