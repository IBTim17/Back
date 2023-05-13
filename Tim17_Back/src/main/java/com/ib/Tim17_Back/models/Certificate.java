package com.ib.Tim17_Back.models;

import com.ib.Tim17_Back.enums.CertificateType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "Certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private User owner;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Certificate issuer;
    @Column
    private String serialNumber;
    @Enumerated(EnumType.STRING)
    @Column
    private CertificateType type;
    @Column
    private LocalDateTime startDate;
    @Column
    private LocalDateTime endDate;
    @Column
    private boolean isValid;
    @Column
    private String organization;
    @Column
    private String revokingReason;

    public Certificate(User owner, Certificate issuer, String serialNumber, CertificateType type, LocalDateTime startDate, LocalDateTime endDate, boolean isValid, String organization) {
        this.owner = owner;
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isValid = isValid;
        this.organization = organization;
    }
}
