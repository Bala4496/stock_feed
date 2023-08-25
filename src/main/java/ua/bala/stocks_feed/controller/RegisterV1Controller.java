package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.RegisterUserDTO;
import ua.bala.stocks_feed.mapper.RegisterUserMapper;
import ua.bala.stocks_feed.service.RegisterService;

@Slf4j
@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterV1Controller {

    private final RegisterService registerService;
    private final RegisterUserMapper registerUserMapper;

    @PostMapping
    public Mono<RegisterUserDTO> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        return registerService.registerUser(registerUserMapper.map(registerUserDTO))
                .map(registerUserMapper::map);
    }
}
