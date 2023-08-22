package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ua.bala.stocks_feed.dto.CompanyDTO;
import ua.bala.stocks_feed.mapper.CompanyMapper;
import ua.bala.stocks_feed.service.CompanyService;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @GetMapping
    public Flux<CompanyDTO> receiveCompanies() {
        return companyService.getCompanies()
                .map(companyMapper::map);
    }

}
