package ua.bala.stocks_feed.configuration;

import io.r2dbc.spi.ConnectionFactory;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.r2dbc.R2dbcLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.time.Instant;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ShedLockConfig {

    public static final String CREATE_API_KEY_LOCK_NAME = "createApiKeyTask";

    @Bean
    protected LockProvider getLockProvider(ConnectionFactory connectionFactory) {
        return new R2dbcLockProvider(connectionFactory);
    }

    @Bean
    public LockConfiguration apiKeyTaskLockConfig() {
        return new LockConfiguration(Instant.now(),
                CREATE_API_KEY_LOCK_NAME,
                Duration.ofMinutes(1),
                Duration.ofSeconds(15));
    }

}
