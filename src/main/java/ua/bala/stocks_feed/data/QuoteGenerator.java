package ua.bala.stocks_feed.data;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.CompanyService;
import ua.bala.stocks_feed.service.QuoteService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuoteGenerator {

    private final QuoteService quoteService;
    private final CompanyService companyService;

    @Value("${quote-generation.enabled}")
    private boolean quoteGenerationEnabled;

    @Scheduled(fixedDelayString = "${quote-generation.delay}")
    public void generateStockQuotes() {
        if (!quoteGenerationEnabled) {
            return;
        }

        companyService.getCompanies()
                .map(Company::getCode)
                .flatMap(quoteService::getQuoteByCode)
                .map(this::generateQuote)
                .flatMap(quoteService::save)
                .subscribe();
    }

    private Quote generateQuote(Quote quote) {
        var oldCost = quote.getPrice();
        var error = new BigDecimal("0.5");
        var newCost = new Random().nextBoolean() ? oldCost.add(error) : oldCost.subtract(error);
        newCost = newCost.setScale(2, RoundingMode.HALF_UP);
        return new Quote().setCompanyCode(quote.getCompanyCode()).setPrice(newCost);
    }

}
