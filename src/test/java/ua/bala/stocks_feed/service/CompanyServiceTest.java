package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.repository.CompanyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @MockBean
    CompanyRepository companyRepository;
    @Autowired
    CompanyService companyService;

    @Test
    void saveAll() {
        List<Company> testCompanies = List.of(getTestCompany());

        when(companyRepository.saveAll(testCompanies)).thenReturn(Flux.just(getTestCompany()));

        Company blockedCompany = companyService.saveAll(testCompanies).blockFirst();

        assertNotNull(blockedCompany);
    }

    @Test
    void save() {
        Company company = getTestCompany();

        when(companyRepository.save(company)).thenReturn(Mono.just(company));

        Company blockedCompany = companyRepository.save(company).block();

        assertNotNull(blockedCompany);
    }

    @Test
    void getCompanies() {
        when(companyRepository.findAll()).thenReturn(Flux.just(getTestCompany()));

        Company blockedCompany = companyService.getCompanies().blockFirst();

        assertNotNull(blockedCompany);
    }

    @Test
    void getCompanyByCode() {
        Company company = getTestCompany();
        String code = company.getCode();

        when(companyRepository.findByCode(code)).thenReturn(Mono.just(company));

        Company blockedCompany = companyService.getCompanyByCode(code).block();

        assertEquals(company, blockedCompany);
    }

    @Test
    void getCount() {
        when(companyRepository.count()).thenReturn(Mono.just(1L));

        Long companiesCount = companyService.getCount().block();

        assertEquals(1L, companiesCount);
    }

    private Company getTestCompany() {
        return new Company()
                .setId(1L)
                .setName("Test company")
                .setCode("TEST");
    }
}