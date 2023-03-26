package com.ib.Tim17_Back.pki.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PublicKey;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerData {
    private String name;
    private PublicKey publicKey;
    private String serialNumber;
    private Date startDate;
    private Date endDate;
}
