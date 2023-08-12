package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.bala.stocks_feed.dto.UserDto;
import ua.bala.stocks_feed.mapper.UserMapper;
import ua.bala.stocks_feed.service.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDto userDto) {
        log.info("Registration of user");
        userService.registerUser(UserMapper.INSTANCE.map(userDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

}
