package com.kvantino.ServeYouApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phoneNumber;
}
