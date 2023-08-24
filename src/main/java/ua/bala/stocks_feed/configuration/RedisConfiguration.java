package ua.bala.stocks_feed.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

@Configuration
public class RedisConfiguration {

    public static final String COMPANY_KEY_PREFIX = "company";
    public static final String QUOTE_KEY_PREFIX = "quote";

    @Bean
    public ReactiveRedisTemplate<String, Company> companyReactiveRedisOperations(ReactiveRedisConnectionFactory factory,
                                                                                  ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Company> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Company.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Company> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Company> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, Quote> quoteReactiveRedisOperations(ReactiveRedisConnectionFactory factory,
                                                                             ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Quote> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Quote.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Quote> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Quote> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
