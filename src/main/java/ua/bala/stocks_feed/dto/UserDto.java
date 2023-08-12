package ua.bala.stocks_feed.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class UserDto {

    private String username;
    @ToString.Exclude
    private String password;
}
