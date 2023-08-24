package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.ApiKeyDTO;
import ua.bala.stocks_feed.mapper.ApiKeyMapper;
import ua.bala.stocks_feed.service.ApiKeyService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/api-key")
@RequiredArgsConstructor
public class ApiKeyV1Controller {

    private final ApiKeyService apiKeyService;
    private final ApiKeyMapper apiKeyMapper;

    @PostMapping
    public Mono<ApiKeyDTO> createApiKey(Principal principal) {
        return apiKeyService.createApiKey(principal.getName())
                .map(apiKeyMapper::map);
    }

    @GetMapping
    public Mono<ApiKeyDTO> getApiKey(Principal principal) {
        return apiKeyService.getApiKeyByUsername(principal.getName())
                .map(apiKeyMapper::map);
    }

    @DeleteMapping("/{apiKey}")
    public Mono<Void> disableApiKey(@PathVariable String apiKey) {
        return apiKeyService.deleteApiKey(apiKey);
    }

}
