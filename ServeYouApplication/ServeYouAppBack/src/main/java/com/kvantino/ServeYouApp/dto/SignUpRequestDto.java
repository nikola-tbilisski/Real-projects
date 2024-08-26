package com.kvantino.ServeYouApp.dto;

import com.kvantino.ServeYouApp.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    private Long id;

    private String email;

    private String password;

    private String name;

    private String lastname;

    private String phoneNumber;

    private UserRole userRole;
}
