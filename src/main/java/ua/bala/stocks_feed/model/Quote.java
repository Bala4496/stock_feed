package ua.bala.stocks_feed.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//@RedisHash(QUOTE_KEY)
@Table("quotes")
@Data
@Accessors(chain = true)
public class Quote {

    @Id
    private Long id;
    @Column("company_id")
    private Long companyId;
    private BigDecimal price;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
}