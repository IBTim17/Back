package com.ib.Tim17_Back.enums;

public enum CertificateType {
    ROOT, INTERMEDIATE, END;

    public static CertificateType GetType(String type) {
        if(type.equalsIgnoreCase("ROOT"))
            return ROOT;
        if(type.equalsIgnoreCase("INTERMEDIATE"))
            return INTERMEDIATE;
        return END;
    }
}
