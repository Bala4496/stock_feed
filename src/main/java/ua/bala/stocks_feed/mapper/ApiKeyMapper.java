package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stocks_feed.dto.ApiKeyDTO;
import ua.bala.stocks_feed.model.ApiKey;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    ApiKeyDTO map(ApiKey user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @InheritInverseConfiguration
    ApiKey map(ApiKeyDTO userDto);
}
