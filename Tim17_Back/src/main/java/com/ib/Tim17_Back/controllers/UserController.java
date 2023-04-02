package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.LoginDTO;
import com.ib.Tim17_Back.dtos.TokenDTO;
import com.ib.Tim17_Back.models.ErrorResponseMessage;
import com.ib.Tim17_Back.security.SecurityUser;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@CrossOrigin
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> logIn(@Valid @RequestBody LoginDTO login) {
        try {

            TokenDTO token = new TokenDTO();
            SecurityUser userDetails = (SecurityUser) this.userService.findByUsername(login.getEmail());

//            boolean isEmailConfirmed = this.passengerService.getIsEmailConfirmed(login.getEmail());

            String tokenValue = this.jwtTokenUtil.generateToken(userDetails);
            token.setToken(tokenValue);
            token.setRefreshToken(this.jwtTokenUtil.generateRefreshToken(userDetails));
            Authentication authentication =
                    this.authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(login.getEmail(),
                                    login.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorResponseMessage(
                    "Bad credentials"), HttpStatus.BAD_REQUEST);
        }

    }


}
