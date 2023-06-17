package com.ib.Tim17_Back.dtos;

import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.models.CertificateRequest;
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
    private String serialNumber;

    public CertificateDTO(Certificate certificate){
        this.type = certificate.getType().toString();
        this.startDate = certificate.getStartDate();
        this.ownerEmail = certificate.getOwner().getEmail();
        this.ownerName = certificate.getOwner().getFirstName();
        this.ownerLastName = certificate.getOwner().getLastName();
        this.serialNumber = certificate.getSerialNumber();
    }

    public CertificateDTO(CertificateRequest request) {
        this.type = request.getType().toString();
        //this.startDate = request.getStartDate();
        this.ownerEmail = request.getOwner().getEmail();
        this.ownerName = request.getOwner().getFirstName();
        this.ownerLastName = request.getOwner().getLastName();
        this.serialNumber = null;
    }
}
