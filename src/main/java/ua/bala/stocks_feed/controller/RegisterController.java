package ua.bala.stocks_feed.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.UserDto;
import ua.bala.stocks_feed.mapper.UserMapper;
import ua.bala.stocks_feed.service.RegisterService;

@Slf4j
@RestController
@RequestMapping("/api/v1/register")
@AllArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    private final UserMapper userMapper;

    @PostMapping
    public Mono<UserDto> registerUser(@RequestBody UserDto userDto) {
        log.info("Registration of user");
        return registerService.registerUser(userMapper.map(userDto))
                .map(userMapper::map);
    }
}
