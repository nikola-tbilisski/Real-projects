package com.kvantino.ServeYouApp.services.authentication;

import com.kvantino.ServeYouApp.dto.SignUpRequestDto;
import com.kvantino.ServeYouApp.dto.UserDto;

public interface AuthService {
    UserDto signupClient(SignUpRequestDto signUpRequestDto);
}
