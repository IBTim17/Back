package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.models.ErrorResponseMessage;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Controller
@CrossOrigin
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> logIn(@Valid @RequestBody LoginDTO login) {
        try {
            logger.info("Started login process");
            TokenDTO token = this.userService.logIn(login.getEmail(), login.getPassword());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Login failed");
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserDTO registeredUserDTO = null;
        try {
            logger.info("Started registration");
            registeredUserDTO = this.userService.register(createUserDTO);
        } catch (NoSuchAlgorithmException e) {
            logger.info("Registration failed");
            return new ResponseEntity(new ErrorResponseMessage(
                    "Something went wrong!"), HttpStatus.BAD_REQUEST);
        }
        logger.info("Successful registration");
        return new ResponseEntity<>(registeredUserDTO, HttpStatus.OK);
    }

    @PutMapping("/confirm")
    public ResponseEntity<String> confirm(@Valid @RequestBody AccountConfirmationDTO accountConfirmationDTO) {
        try {
            this.userService.confirmAccount(accountConfirmationDTO);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorResponseMessage(
                    "Something went wrong!"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Account confirmed", HttpStatus.OK);
    }

    @PostMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendPasswordResetEmail(@Valid @RequestBody ResetPasswordDTO dto) throws IOException {
        userService.sendPasswordResetCode(dto);
        return new ResponseEntity<>("Reset code has been sent!",HttpStatus.OK);
    }

    @PutMapping(value = "/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequest) throws Exception {
        userService.resetPassword(passwordResetRequest);
        return new ResponseEntity<>("Password successfully changed!",HttpStatus.OK);
    }
}
