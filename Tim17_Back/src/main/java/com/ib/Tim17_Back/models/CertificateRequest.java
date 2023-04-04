package com.ib.Tim17_Back.models;

import com.ib.Tim17_Back.enums.CertificateRequestState;
import com.ib.Tim17_Back.enums.CertificateType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "CertificateRequests")
public class CertificateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private CertificateType type;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private User owner;
    private String issuerSN;
    @Column
    private String organization;
    @Column
    private CertificateRequestState state;
    @Column
    private String rejectReason;
}
