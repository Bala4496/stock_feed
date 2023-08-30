package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stocks_feed.dto.RegisterUserDTO;
import ua.bala.stocks_feed.model.User;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    @Mapping(target = "password", ignore = true)
    RegisterUserDTO map(User user);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User map(RegisterUserDTO registerUserDTO);
}
