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

import javax.validation.Valid;
import java.io.IOException;
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
            TokenDTO token = this.userService.logIn(login.getEmail(), login.getPassword(), login.getResource());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkLoginCode(@Valid @RequestBody LoginCodeDTO dto) {
        userService.checkLoginCode(dto);
        return new ResponseEntity<>("Login code is valid!", HttpStatus.OK);
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

    @PutMapping("/confirm")
    public ResponseEntity<String> confirm(@Valid @RequestBody AccountConfirmationDTO accountConfirmationDTO) {
        System.out.println("uso1");
        try {
            System.out.println("uso2");
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
    @PostMapping(value = "/recaptcha/{token}")
    public ResponseEntity<Boolean> recaptcha(@Valid @PathVariable(value = "token", required = true)String token){
        return new ResponseEntity<>(this.userService.verifyRecaptcha(token), HttpStatus.OK);
    }
}
