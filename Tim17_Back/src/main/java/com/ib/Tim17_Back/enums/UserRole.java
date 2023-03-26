package com.ib.Tim17_Back.enums;

public enum UserRole {
    ADMIN, USER, UNREGISTERED_USER;

    public static UserRole GetRole(String role) {
        if(role.equalsIgnoreCase("ADMIN"))
            return ADMIN;
        if(role.equalsIgnoreCase("USER"))
            return USER;
        return UNREGISTERED_USER;
    }
}
