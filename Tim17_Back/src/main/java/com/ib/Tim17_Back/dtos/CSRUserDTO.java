package com.ib.Tim17_Back.dtos;

import com.ib.Tim17_Back.enums.CertificateRequestState;
import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CSRUserDTO {
    private CertificateType type;
    private String issuer;
    private String organization;
    private CertificateRequestState state;
    public CSRUserDTO(CertificateRequest request) {
        this.setType(request.getType());
        this.setIssuer(request.getIssuerSN());
        this.setOrganization(request.getOrganization());
        this.setState(request.getState());
    }
}
