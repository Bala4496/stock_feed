package ua.bala.stocks_feed.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.repository.CompanyRepository;
import ua.bala.stocks_feed.repository.CompanyRepositoryRedis;

@Service
@Primary
@ConditionalOnProperty(name = "redis-cache.enabled", havingValue = "true")
public class CompanyServiceWithRedisCache extends CompanyService {

    private final CompanyRepositoryRedis companyRepositoryRedis;

    public CompanyServiceWithRedisCache(CompanyRepository companyRepository,
                                        CompanyRepositoryRedis companyRepositoryRedis) {
        super(companyRepository);
        this.companyRepositoryRedis = companyRepositoryRedis;
    }

    @Override
    public Flux<Company> saveAll(Iterable<Company> companies) {
        return super.saveAll(companies)
                .flatMap(companyRepositoryRedis::save);
    }

    @Override
    public Mono<Company> save(Company company) {
        return super.save(company)
                .flatMap(companyRepositoryRedis::save);
    }

    @Override
    public Flux<Company> getCompanies() {
        return companyRepositoryRedis.findAll()
                .switchIfEmpty(super.getCompanies()
                        .flatMap(companyRepositoryRedis::save)
                );
    }

    @Override
    public Mono<Company> getCompanyByCode(String code) {
        return companyRepositoryRedis.findByCode(code)
                .switchIfEmpty(super.getCompanyByCode(code)
                        .flatMap(companyRepositoryRedis::save));
    }
}