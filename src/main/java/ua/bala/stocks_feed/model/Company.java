package ua.bala.stocks_feed.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("companies")
@Data
@Accessors(chain = true)
public class Company {

    @Id
    private Long id;
    private String code;
    private String name;
}
