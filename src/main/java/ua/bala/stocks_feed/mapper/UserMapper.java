package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stocks_feed.dto.UserDTO;
import ua.bala.stocks_feed.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO map(User user);

    @InheritInverseConfiguration
    @Mapping(target = "authorities", ignore = true)
    User map(UserDTO userDto);
}
