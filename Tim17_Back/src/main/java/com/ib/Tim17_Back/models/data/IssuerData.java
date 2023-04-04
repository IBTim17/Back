package com.ib.Tim17_Back.models.data;

import lombok.*;
import org.bouncycastle.asn1.x500.X500Name;


import java.security.PrivateKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class IssuerData {
    private X500Name x500name;
    private PrivateKey privateKey;
}
