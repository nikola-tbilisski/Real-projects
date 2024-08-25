package com.kvantino.ServeYouApp.utils.mapper;

import com.kvantino.ServeYouApp.dto.SignUpRequestDto;
import com.kvantino.ServeYouApp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    SignUpRequestDto userToSignUpRequestDto(User user);
    User signUpRequestDtoToUser(SignUpRequestDto signUpRequestDto);

    @Mapping(target = "userRole", ignore = true)
    SignUpRequestDto createSignUpRequestDtoWithoutUserRole(SignUpRequestDto signUpRequestDto);
}
