package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.services.CertificateRequestService;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import com.ib.Tim17_Back.validations.UserRequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping("api/requests")
public class CertificateRequestController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRequestValidation userRequestValidation;

    @Autowired
    ICertificateRequestService certificateRequestService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping()
    public ResponseEntity<?> userRequests(@RequestHeader Map<String, String> headers){
        Optional<User> user = userRepository.findById(userRequestValidation.getUserId(headers));
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<CSRUserDTO> usersRequests = certificateRequestService.getUsersRequests(user.get());
        return new ResponseEntity<>(usersRequests,HttpStatus.OK);
    }
}
