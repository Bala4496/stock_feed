package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stocks_feed.dto.QuoteDTO;
import ua.bala.stocks_feed.model.Quote;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    QuoteDTO map(Quote user);

    @Mapping(target = "id", ignore = true)
    @InheritInverseConfiguration
    Quote map(QuoteDTO userDto);
}
