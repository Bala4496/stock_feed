package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.UserDto;
import ua.bala.stocks_feed.mapper.UserMapper;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/api-key")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public Mono<String> generateApiKey(@RequestBody UserDto userDto) {
        log.info("Generating of ApiKey");
        User user = UserMapper.INSTANCE.map(userDto);
        return authenticationService.generateApiKey(user);
    }

}
