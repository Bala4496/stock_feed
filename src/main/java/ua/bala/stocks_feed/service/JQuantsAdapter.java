package ua.bala.stocks_feed.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class JQuantsAdapter implements ExchangeAdapter {

    @Value("${jquants.mail_address}")
    private String mailAddress;
    @Value("${jquants.password}")
    private String password;
    private final static String HOST_URL = "https://api.jquants.com/v1";
    private final static String REFRESH_TOKEN_API = "/token/auth_user";
    private final static String ACCESS_TOKEN_API = "/token/auth_refresh";
    private final static String COMPANIES_INFO_API = "/listed/info";
    private final static String STOCK_PRICES_API = "/prices/daily_quotes";
    private final WebClient webClient;
    @Autowired
    @Lazy
    private JQuantsAdapter jQuantsAdapter;

    public JQuantsAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(HOST_URL).build();
    }

    @Cacheable("refreshTokenCache")
    @Override
    public Mono<String> getRefreshToken() {
        log.info("Fetching RefreshToken");
        return webClient.post()
                .uri(REFRESH_TOKEN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"mailaddress\": \"%s\", \"password\": \"%s\"}".formatted(mailAddress, password))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> node.get("refreshToken").asText())
                .doOnNext(a -> log.info("Fetched RefreshToken"))
                .doOnError(a -> log.info("Error by fetching RefreshToken"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No RefreshToken received")));
    }

    @Cacheable("accessTokenCache")
    @Override
    public Mono<String> getAccessToken() {
        log.info("Fetching AccessToken");
        return jQuantsAdapter.getRefreshToken()
                .flatMap(refreshToken -> webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(ACCESS_TOKEN_API)
                                .queryParam("refreshtoken", refreshToken)
                                .build()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .map(node -> node.get("idToken").asText())
                        .doOnNext(a -> log.info("Fetched AccessToken"))
                        .doOnError(a -> log.info("Error by fetching AccessToken"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No AccessToken received")))
                );
    }

//    @Cacheable("companiesCache")
    @Override
    public Flux<Company> getCompanies() {
        return jQuantsAdapter.getAccessToken()
                .flatMapMany(accessToken -> webClient.get()
                        .uri(COMPANIES_INFO_API)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .retrieve()
                        .bodyToFlux(DataBuffer.class)
                        .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
                        .collectList()
                        .map(strings -> String.join("", strings))
                        .flatMapMany(jsonString -> {
                            try {
                                return Flux.fromIterable(new ObjectMapper().readTree(String.valueOf(jsonString)).get("info"));
                            } catch (Exception e) {
                                return Flux.empty();
                            }
                        })
                        .map(node -> new Company(node.get("Code").asText(), node.get("CompanyNameEnglish").asText()))
                        .doFinally(signalType -> log.info("Companies uploaded"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No Companies received")))
                );
    }

//    @Cacheable("stockQuotesCache")
    @Override
    public Mono<Quote> getStockQuotesByCode(Integer stockCode) {
        return jQuantsAdapter.getAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(STOCK_PRICES_API)
                                .queryParam("code", stockCode)
                                .build()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .flatMapMany(jsonNode -> Flux.fromStream(StreamSupport.stream(jsonNode.get("daily_quotes").spliterator(), true)))
                        .map(node -> new Quote(node.get("Date").asText(), node.get("Code").asText(), node.get("Open").asDouble()))
                        .sort((e1, e2) -> e2.date().compareTo(e1.date()))
                        .next()
                        .doOnError(throwable -> log.error("Error while getStockQuotesByCode", throwable))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No Stock quotes received")))
                );
    }
}
