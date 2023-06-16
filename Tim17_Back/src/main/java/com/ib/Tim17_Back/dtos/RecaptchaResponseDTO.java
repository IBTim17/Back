package com.ib.Tim17_Back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecaptchaResponseDTO {
    private boolean success;
    private String challenge_ts;
    private String hostname;
}
