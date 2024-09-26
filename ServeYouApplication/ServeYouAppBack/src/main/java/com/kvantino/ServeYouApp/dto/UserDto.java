package com.kvantino.ServeYouApp.dto;

import com.kvantino.ServeYouApp.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private UserRole userRole;
}
