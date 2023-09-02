package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @MockBean
    QuoteRepository quoteRepository;
    @Autowired
    QuoteService quoteService;

    @Test
    void getQuoteByCode() {
        Quote quote = getTestQuote();
        String companyCode = quote.getCompanyCode();

        when(quoteRepository.findFirstByCompanyCodeOrderByCreatedAtDesc(companyCode)).thenReturn(Mono.just(quote));

        Quote block = quoteService.getQuoteByCode(companyCode).block();

        assertNotNull(block);
    }

    @Test
    void save() {
        Quote quote = getTestQuote();

        when(quoteRepository.save(quote)).thenReturn(Mono.just(quote));

        Quote blockedQuote = quoteService.save(quote).block();

        assertEquals(quote, blockedQuote);
    }

    @Test
    void getCount() {
        when(quoteRepository.count()).thenReturn(Mono.just(1L));

        Long count = quoteService.getCount().block();

        assertEquals(1L, count);
    }

    private Quote getTestQuote() {
        return new Quote()
                .setId(1L)
                .setCompanyCode("TEST")
                .setPrice(new BigDecimal("100.00"));
    }
}