package ua.bala.stocks_feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StocksFeedProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(StocksFeedProjectApplication.class, args);
    }

}
