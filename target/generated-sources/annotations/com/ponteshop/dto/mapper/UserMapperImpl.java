package com.ponteshop.dto.mapper;

import com.ponteshop.dto.UserDto;
import com.ponteshop.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-22T19:42:49+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.name( user.getName() );
        userDto.email( user.getEmail() );
        userDto.phone( user.getPhone() );
        userDto.role( user.getRole() );
        userDto.city( user.getCity() );
        userDto.address( user.getAddress() );
        userDto.createdAt( user.getCreatedAt() );

        return userDto.build();
    }
}
