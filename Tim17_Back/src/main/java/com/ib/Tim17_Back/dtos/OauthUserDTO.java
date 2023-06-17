package com.ib.Tim17_Back.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OauthUserDTO {
    private String name;
    private String lastName;
    private String email;
}
