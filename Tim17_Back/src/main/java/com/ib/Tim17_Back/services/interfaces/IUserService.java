package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.exceptions.IncorrectCodeException;
import com.ib.Tim17_Back.exceptions.InvalidRecaptchaException;
import com.ib.Tim17_Back.exceptions.UserNotFoundException;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public interface IUserService{
    UserDetails findByUsername(String email);

    TokenDTO logIn(String email, String password) throws Exception;

    UserDTO register(CreateUserDTO createUserDTO) throws NoSuchAlgorithmException;

    void sendPasswordResetCode(ResetPasswordDTO body) throws NoSuchAlgorithmException, MessagingException, IOException;

    void resetPassword(PasswordResetRequestDTO passwordResetRequest) throws IncorrectCodeException, UserNotFoundException;

    Optional<User> findById(Long userId);

    void confirmAccount(AccountConfirmationDTO accountConfirmationDTO);
    boolean checkPasswordRenewal(String token);

    Boolean verifyRecaptcha(String token) throws InvalidRecaptchaException;
}
