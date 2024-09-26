package com.kvantino.ServeYouApp.services.authentication;

import com.kvantino.ServeYouApp.dto.SignUpRequestDto;
import com.kvantino.ServeYouApp.dto.UserDto;
import com.kvantino.ServeYouApp.enums.UserRole;
import com.kvantino.ServeYouApp.model.User;
import com.kvantino.ServeYouApp.repository.UserRepository;
import com.kvantino.ServeYouApp.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto signupClient(SignUpRequestDto signUpRequestDto) {
        UserDto userDto = userMapper.userAuthRequestDto(signUpRequestDto);
        userDto.setUserRole(UserRole.CLIENT);

        userRepository.save(userMapper.userDtoToUser(userDto));
        return userDto;
    }

//    public UserDto signupClient(SignUpRequestDto signUpRequestDto) {
//        UserDto userDto = userMapper.userAuthRequestDto(signUpRequestDto);
//
//        userRepository.save(userMapper.userDtoToUser(userDto));
//        return userDto;
//    }
}
