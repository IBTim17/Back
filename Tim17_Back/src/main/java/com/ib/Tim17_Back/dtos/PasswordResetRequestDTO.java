package com.ib.Tim17_Back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordResetRequestDTO {
    private String email;
    private String newPassword;
    private String code;
}
