package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;
import ua.bala.stocks_feed.repository.QuoteRepositoryRedis;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class QuoteServiceWithRedisCacheTest {

    @MockBean
    QuoteRepository quoteRepository;
    @MockBean
    QuoteRepositoryRedis quoteRepositoryRedis;
    @Autowired
    QuoteService quoteService;

    @Test
    void getQuoteByCode() {
        Quote quote = getTestQuote();
        String companyCode = quote.getCompanyCode();

        when(quoteRepository.findFirstByCompanyCodeOrderByCreatedAtDesc(companyCode)).thenReturn(Mono.empty());
        when(quoteRepositoryRedis.findByCompanyCode(companyCode)).thenReturn(Mono.just(quote));

        Quote block = quoteService.getQuoteByCode(companyCode).block();

        assertNotNull(block);
    }

    @Test
    void save() {
        Quote quote = getTestQuote();

        when(quoteRepository.save(quote)).thenReturn(Mono.just(quote));
        when(quoteRepositoryRedis.save(quote)).thenReturn(Mono.just(quote));

        Quote blockedQuote = quoteService.save(quote).block();

        assertEquals(quote, blockedQuote);
    }

    private Quote getTestQuote() {
        return new Quote()
                .setId(1L)
                .setCompanyCode("TEST")
                .setPrice(new BigDecimal("100.00"));
    }
}