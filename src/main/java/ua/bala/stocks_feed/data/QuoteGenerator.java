package ua.bala.stocks_feed.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.CompanyService;
import ua.bala.stocks_feed.service.QuoteService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.function.BinaryOperator;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteGenerator {

    private final QuoteService quoteService;
    private final CompanyService companyService;
    private Random random = new Random();

    @Value("${quote-generation.enabled}")
    private boolean quoteGenerationEnabled;

    @Scheduled(fixedDelayString = "${quote-generation.delay}")
    public void generateStockQuotes() {
        if (!quoteGenerationEnabled) {
            return;
        }
        var now = LocalDateTime.now();
        companyService.getCompanies()
                .map(Company::getCode)
                .flatMap(quoteService::getQuoteByCode)
                .map(this::generateQuote)
                .map(quote -> quote.setCreatedAt(now))
                .flatMap(quoteService::save)
                .doFinally(w -> log.info("Quotes updated"))
                .subscribe();
    }

    private Quote generateQuote(Quote quote) {
        BinaryOperator<BigDecimal> operation = switch (random.nextInt(3) - 1) {
            case 1 -> BigDecimal::add;
            case -1 -> BigDecimal::subtract;
            default -> null;
        };
        return Optional.ofNullable(operation)
                .map(op -> quote.setPrice(op.apply(quote.getPrice(), BigDecimal.valueOf(random.nextInt(11))).abs()))
                .orElse(quote);
    }

}
