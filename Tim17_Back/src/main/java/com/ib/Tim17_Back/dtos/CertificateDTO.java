package com.ib.Tim17_Back.dtos;

import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.models.Certificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private String type;
    private LocalDateTime startDate;
    private String ownerEmail;
    private String ownerName;
    private String ownerLastName;

    public CertificateDTO(Certificate certificate){
        this.type = certificate.getType().toString();
        this.startDate = certificate.getStartDate();
        this.ownerEmail = certificate.getOwner().getEmail();
        this.ownerName = certificate.getOwner().getFirstName();
        this.ownerLastName = certificate.getOwner().getLastName();
    }
}
