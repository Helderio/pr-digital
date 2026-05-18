package com.ponteshop.dto.mapper;

import com.ponteshop.dto.UserDto;
import com.ponteshop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}

