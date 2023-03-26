package com.ib.Tim17_Back.models;

import com.ib.Tim17_Back.enums.CertificateType;

import javax.persistence.*;
import java.util.Date;

public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Enumerated(EnumType.STRING)
    @Column
    public CertificateType type;
    @Column
    public Date startDate;
    @Column
    public Date endDate;
    @Column
    public boolean isValid;

}
