package com.ib.Tim17_Back.dtos;

import com.ib.Tim17_Back.enums.CertificateType;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRequestDTO {
    private CertificateType type;
    private String issuer;
    private String organization;
}
