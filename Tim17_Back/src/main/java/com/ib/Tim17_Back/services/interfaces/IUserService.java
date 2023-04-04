package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CreateUserDTO;
import com.ib.Tim17_Back.dtos.TokenDTO;
import com.ib.Tim17_Back.dtos.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.NoSuchAlgorithmException;

public interface IUserService{
    UserDetails findByUsername(String email);

    TokenDTO logIn(String email, String password) throws Exception;

    UserDTO register(CreateUserDTO createUserDTO) throws NoSuchAlgorithmException;

}
