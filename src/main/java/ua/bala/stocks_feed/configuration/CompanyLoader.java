package ua.bala.stocks_feed.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.CompanyService;
import ua.bala.stocks_feed.service.FileReaderService;
import ua.bala.stocks_feed.service.QuoteService;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompanyLoader {

    private final FileReaderService fileReaderService;
    private final CompanyService companyService;
    private final QuoteService quoteService;

    @PostConstruct
    public void loadData() {
        companyService.getCount()
                .filter(count -> count.equals(0L))
                .flatMapMany(count -> fileReaderService.readCompaniesFromFile())
                .flatMap(companyService::save)
                .subscribe();
        quoteService.getCount()
                .filter(count -> count.equals(0L))
                .flatMapMany(count -> companyService.getCompanies())
                .map(company -> new Quote().setCompanyId(company.getId()).setPrice(new BigDecimal("100.00")))
                .flatMap(quoteService::save)
                .subscribe();
    }

}