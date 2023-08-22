package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.UserDTO;
import ua.bala.stocks_feed.mapper.UserMapper;
import ua.bala.stocks_feed.service.RegisterService;

@Slf4j
@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    private final UserMapper userMapper;

    @PostMapping
    public Mono<UserDTO> registerUser(@RequestBody UserDTO userDto) {
        log.info("Registration of user");
        return registerService.registerUser(userMapper.map(userDto))
                .map(userMapper::map);
    }
}
