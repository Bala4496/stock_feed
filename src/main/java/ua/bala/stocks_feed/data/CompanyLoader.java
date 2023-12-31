package ua.bala.stocks_feed.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.CompanyService;
import ua.bala.stocks_feed.service.QuoteService;

import java.math.BigDecimal;
import java.util.function.Predicate;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "company.load", havingValue = "true")
public class CompanyLoader {

    private final FileReaderService fileReaderService;
    private final CompanyService companyService;
    private final QuoteService quoteService;
    private final Predicate<Long> isEmpty = (Long count) -> count.equals(0L);

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        companyService.getCount()
                .filter(isEmpty)
                .flatMapMany(count -> fileReaderService.readCompaniesFromFile())
                .flatMap(companyService::save)
                .then()
                .doFinally(w -> log.info("Companies updated"))
                .block();
        quoteService.getCount()
                .filter(isEmpty)
                .flatMapMany(count -> companyService.getCompanies())
                .map(Company::getCode)
                .map(companyCode -> new Quote().setCompanyCode(companyCode).setPrice(new BigDecimal("100.00")))
                .flatMap(quoteService::save)
                .doFinally(w -> log.info("Quotes uploaded"))
                .subscribe();
    }

}