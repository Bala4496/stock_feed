package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.repository.CompanyRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Flux<Company> saveAll(Iterable<Company> companies) {
        return companyRepository.saveAll(companies);
    }

    public Mono<Company> save(Company company) {
        return companyRepository.save(company);
    }

    public Flux<Company> getCompanies() {
        return companyRepository.findAll();
    }

    public Mono<Company> getCompanyByCode(String code) {
        return companyRepository.findByCode(code);
    }

    public Mono<Long> getCount() {
        return companyRepository.count();
    }

}
