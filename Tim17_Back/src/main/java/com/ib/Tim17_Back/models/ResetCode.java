package com.ib.Tim17_Back.models;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4)
    String code;

    @Column
    LocalDateTime expirationDate;

    public ResetCode(Long id, String code, LocalDateTime expirationDate) {
        this.id = id;
        this.code = code;
        this.expirationDate = expirationDate;
    }

    public ResetCode(String code, LocalDateTime expirationDate) {
        this.code = code;
        this.expirationDate = expirationDate;
    }

    public ResetCode() {
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
