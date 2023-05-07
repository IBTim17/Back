package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.exceptions.IncorrectCodeException;
import com.ib.Tim17_Back.exceptions.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface IUserService{
    UserDetails findByUsername(String email);

    TokenDTO logIn(String email, String password) throws Exception;

    UserDTO register(CreateUserDTO createUserDTO) throws NoSuchAlgorithmException;

    void sendPasswordResetCode(ResetPasswordDTO body) throws NoSuchAlgorithmException, MessagingException, IOException;

    void resetPassword(PasswordResetRequestDTO passwordResetRequest) throws IncorrectCodeException, UserNotFoundException;

}
