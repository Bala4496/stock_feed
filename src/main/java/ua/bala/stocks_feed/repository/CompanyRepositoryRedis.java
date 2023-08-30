package ua.bala.stocks_feed.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;

import java.util.function.UnaryOperator;

import static ua.bala.stocks_feed.configuration.RedisConfig.COMPANY_KEY_PREFIX;

@Slf4j
@Repository
public class CompanyRepositoryRedis {

    private final ReactiveRedisTemplate<String, Company> companyReactiveRedisTemplate;
    private final UnaryOperator<String> prefixApplier = (String key) -> String.join("#", COMPANY_KEY_PREFIX, key);

    public CompanyRepositoryRedis(ReactiveRedisTemplate<String, Company> companyReactiveRedisTemplate) {
        this.companyReactiveRedisTemplate = companyReactiveRedisTemplate;
    }

    public Mono<Company> findByCode(String code) {
        return companyReactiveRedisTemplate.opsForValue().get(prefixApplier.apply(code));
    }

    public Flux<Company> findAll() {
        return companyReactiveRedisTemplate.keys(prefixApplier.apply("*"))
                .flatMap(this::findByCode);
    }

    public Mono<Company> save(Company company) {
        return companyReactiveRedisTemplate.opsForValue().set(prefixApplier.apply(company.getCode()), company)
                .thenReturn(company);
    }

    public Mono<Boolean> deleteByCode(String code) {
        return companyReactiveRedisTemplate.opsForValue().delete(code);
    }
}



