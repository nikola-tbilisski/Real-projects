package com.kvantino.ServeYouApp.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private Long id;

    private String email;

    private String password;

    private String name;

    private String lastname;

    private String phoneNumber;
}
