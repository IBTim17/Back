package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRequestRepository;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import com.ib.Tim17_Back.validations.UserRequestValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @Autowired
    CertificateRequestRepository certificateRequestRepository;

    private static final Logger logger = LogManager.getLogger(CertificateRequestController.class);
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/list-all")
    public ResponseEntity<?> userRequests(@RequestHeader Map<String, String> headers){
        Optional<User> user = userRepository.findById(Long.valueOf(userRequestValidation.getUserId(headers)));
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<CertificateDTO> usersRequests = certificateRequestService.getUsersRequests(user.get());
        return new ResponseEntity<>(usersRequests,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createRequests(@Valid @RequestBody CertificateRequestDTO body, @RequestHeader Map<String, String> headers) {
        CSRUserDTO request = certificateRequestService.createRequest(body, headers);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveCSR(@RequestHeader Map<String, String> headers, @PathVariable(value = "id", required = true) @NotNull Long id){
        MessageResponseDTO approved = certificateRequestService.approveCSR(id, userRequestValidation.getUserId(headers));
        return new ResponseEntity<>(approved, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping(value = "/decline/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> declineCSR(@RequestHeader Map<String, String> headers, @PathVariable(value = "id", required = true) @NotNull Long id, @RequestBody RejectionDTO reason){
        MessageResponseDTO responseDTO = certificateRequestService.declineCSR(id,userRequestValidation.getUserId(headers),reason);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
