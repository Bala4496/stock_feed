package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stocks_feed.dto.CompanyDTO;
import ua.bala.stocks_feed.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDTO map(Company user);

    @Mapping(target = "id", ignore = true)
    @InheritInverseConfiguration
    Company map(CompanyDTO userDto);
}
