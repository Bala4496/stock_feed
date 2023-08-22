package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ua.bala.stocks_feed.dto.CompanyDTO;
import ua.bala.stocks_feed.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDTO map(Company user);

    @InheritInverseConfiguration
    Company map(CompanyDTO userDto);
}
