package com.ib.Tim17_Back.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4)
    String code;

    @Column
    LocalDateTime expirationDate;

    public AuthCode(String code, LocalDateTime plusMinutes) {
        this.code = code;
        this.expirationDate = plusMinutes;
    }
}
