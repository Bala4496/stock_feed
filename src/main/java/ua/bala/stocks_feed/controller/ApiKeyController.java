package ua.bala.stocks_feed.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.UserDto;
import ua.bala.stocks_feed.mapper.UserMapper;
import ua.bala.stocks_feed.service.ApiKeyService;

@Slf4j
@RestController
@RequestMapping("/api/v1/api-key")
@AllArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserMapper userMapper;

    @PostMapping
    public Mono<String> generateApiKey(@RequestBody UserDto userDto) {
        log.info("Getting of ApiKey");
        return apiKeyService.getApiKey(userMapper.map(userDto));
    }

}
