package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.ApiKeyDTO;
import ua.bala.stocks_feed.dto.UserDTO;
import ua.bala.stocks_feed.mapper.ApiKeyMapper;
import ua.bala.stocks_feed.service.ApiKeyService;

@RestController
@RequestMapping("/api/v1/api-key")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final ApiKeyMapper apiKeyMapper;

    @PostMapping
    public Mono<ApiKeyDTO> createApiKey(@RequestBody UserDTO userDto) {
        return apiKeyService.createApiKey(userDto.getUsername())
                .map(apiKeyMapper::map);
    }

    @GetMapping
    public Mono<ApiKeyDTO> getApiKey(@RequestBody UserDTO userDto) {
        return apiKeyService.getApiKeyByUsername(userDto.getUsername())
                .map(apiKeyMapper::map);
    }

    @DeleteMapping("/{apiKey}")
    public Mono<Void> disableApiKey(@PathVariable String apiKey) {
        return apiKeyService.deleteApiKey(apiKey);
    }

}
