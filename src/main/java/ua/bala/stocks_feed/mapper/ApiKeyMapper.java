package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ua.bala.stocks_feed.dto.ApiKeyDTO;
import ua.bala.stocks_feed.model.ApiKey;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    ApiKeyDTO map(ApiKey user);

    @InheritInverseConfiguration
    ApiKey map(ApiKeyDTO userDto);
}
