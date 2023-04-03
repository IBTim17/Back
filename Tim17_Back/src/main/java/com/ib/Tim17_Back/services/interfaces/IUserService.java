package com.ib.Tim17_Back.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService{
    UserDetails findByUsername(String email);
}
