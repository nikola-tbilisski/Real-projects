package com.kvantino.ServeYouApp.utils.mapper;

import com.kvantino.ServeYouApp.dto.SignUpRequestDto;
import com.kvantino.ServeYouApp.dto.UserDto;
import com.kvantino.ServeYouApp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
    UserDto signUpRequestToUserDto(SignUpRequestDto signUpRequestDto);
    User userDtoToUser(UserDto userDto);

    @Mapping(target = "userRole", ignore = true)
    default UserDto userAuthRequestDto(SignUpRequestDto signUpRequestDto) {
        return signUpRequestToUserDto(signUpRequestDto);
    }
}
