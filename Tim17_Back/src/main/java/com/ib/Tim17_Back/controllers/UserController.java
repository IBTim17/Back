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

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> logIn(@Valid @RequestBody LoginDTO login) {
        try {
            TokenDTO token = this.userService.logIn(login.getEmail(), login.getPassword());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserDTO registeredUserDTO = null;
        try {
            registeredUserDTO = this.userService.register(createUserDTO);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity(new ErrorResponseMessage(
                    "Something went wrong!"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(registeredUserDTO, HttpStatus.OK);
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
