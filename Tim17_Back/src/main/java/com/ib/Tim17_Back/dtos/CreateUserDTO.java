package com.ib.Tim17_Back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private String repeatedPassword;
}
