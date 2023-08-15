package ua.bala.stocks_feed.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "user_keys")
@Data
@Accessors(chain = true)
public class UserKey {

    @Id
    private Long id;
    private String key;
    private Long userId;
    private LocalDateTime expiredAt;

}
