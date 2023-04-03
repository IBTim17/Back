package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.TokenDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService{
    UserDetails findByUsername(String email);

    TokenDTO logIn(String email, String password) throws Exception;
}
