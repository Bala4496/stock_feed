package ua.bala.stocks_feed.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;

import java.util.Map;

import static ua.bala.stocks_feed.configuration.RedisConfiguration.COMPANY_KEY;

@Slf4j
@Repository
public class CompanyRepositoryRedis {

    private final ReactiveHashOperations<String, String, Company> companyReactiveRedisOperations;

    public CompanyRepositoryRedis(ReactiveRedisOperations<String, Company> companyReactiveRedisOperations) {
        this.companyReactiveRedisOperations = companyReactiveRedisOperations.opsForHash();
    }

    public Mono<Company> findByCode(String code) {
        return companyReactiveRedisOperations.get(COMPANY_KEY, code);
    }

    public Flux<Company> findAll() {
        return companyReactiveRedisOperations.entries(COMPANY_KEY)
                .map(Map.Entry::getValue);
    }

    public Mono<Company> save(Company company) {
        return companyReactiveRedisOperations.put(COMPANY_KEY, company.getCode(), company)
                .thenReturn(company);
    }

    public Mono<Boolean> deleteByCode(String code) {
        return companyReactiveRedisOperations.remove(COMPANY_KEY, code).hasElement();
    }
}



