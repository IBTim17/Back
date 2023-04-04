package com.ib.Tim17_Back.exceptions;

public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException() {
        super("Certificate not found!");
    }
}
