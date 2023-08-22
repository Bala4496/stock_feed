package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ua.bala.stocks_feed.model.Company;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class FileReaderService {

    @Value("${company.init-path}")
    private String companyFilePath;
    private final ResourceLoader resourceLoader;

    public Flux<Company> readCompaniesFromFile() {
        try {
            Resource resource = resourceLoader.getResource(companyFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            return Flux.fromStream(reader.lines())
                    .map(line -> line.split(","))
                    .map(parts -> new Company().setCode(parts[0]).setName(parts[1]));
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}
