package ua.bala.stocks_feed.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ua.bala.stocks_feed.dto.UserDto;
import ua.bala.stocks_feed.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto map(User user);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    User map(UserDto userDto);

}
