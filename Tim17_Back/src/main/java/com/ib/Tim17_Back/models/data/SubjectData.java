package com.ib.Tim17_Back.models.data;

import lombok.*;
import org.bouncycastle.asn1.x500.X500Name;


import java.security.PublicKey;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SubjectData {
    private PublicKey publicKey;
    private X500Name x500name;
    private String serialNumber;
    private Date startDate;
    private Date endDate;
}
