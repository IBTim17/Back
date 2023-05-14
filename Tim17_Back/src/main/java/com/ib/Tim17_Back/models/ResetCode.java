package com.ib.Tim17_Back.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4)
    String code;

    @Column
    LocalDateTime expirationDate;

    public ResetCode(String code, LocalDateTime expirationDate) {
        this.code = code;
        this.expirationDate = expirationDate;
    }

}
