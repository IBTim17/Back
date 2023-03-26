package com.ib.Tim17_Back.pki.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PrivateKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssuerData {
    private String name;
    private PrivateKey privateKey;
}
