package com.ib.Tim17_Back.enums;

public enum CertificateRequestState {
    ACCEPTED, DENIED, PENDING;

    public static CertificateRequestState GetState(String type) {
        if(type.equalsIgnoreCase("ACCEPTED"))
            return ACCEPTED;
        if(type.equalsIgnoreCase("DENIED"))
            return DENIED;
        return PENDING;
    }
}
