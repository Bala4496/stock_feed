package ua.bala.stocks_feed.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;
import ua.bala.stocks_feed.model.UserRole;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterUserDTO {

    private String username;
    @ToString.Exclude
    private String password;
    private UserRole role;
}
