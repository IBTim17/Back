package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.models.ErrorResponseMessage;
import com.ib.Tim17_Back.security.SecurityUser;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@CrossOrigin
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static final Logger logger = LogManager.getLogger(UserController.class);


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;


    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil1) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil1;
    }



    @GetMapping("/handleOauth/{email}")
    public void handleGoogleOAuthCallback(@PathVariable("email") String email, HttpServletResponse response) throws IOException {
        System.out.println("EMAIL USO:"+ email);
        TokenDTO token = userService.googleToken(email);
        String redirectUrl;
        redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/main")
                .queryParam("token",token.getToken())
                .queryParam("refresh_token",token.getRefreshToken())
                .toUriString();
        response.sendRedirect(redirectUrl);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> logIn(@Valid @RequestBody LoginDTO login) {
        try {
            TokenDTO token = this.userService.logIn(login.getEmail(), login.getPassword(), login.getResource());
            logger.info("Started login process");
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Login failed");
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
    @PostMapping(value = "/recaptcha/{token}")
    public ResponseEntity<Boolean> recaptcha(@Valid @PathVariable(value = "token", required = true)String token){
        logger.info("User started recaptcha verification");
        return new ResponseEntity<>(this.userService.verifyRecaptcha(token), HttpStatus.OK);
    }
}
