package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.CSRApprovedDTO;
import com.ib.Tim17_Back.dtos.CSRDeclinedDTO;
import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.dtos.CertificateRequestDTO;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRequestRepository;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import com.ib.Tim17_Back.validations.UserRequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/list-all")
    public ResponseEntity<?> userRequests(@RequestHeader Map<String, String> headers){
        Optional<User> user = userRepository.findById(Long.valueOf(userRequestValidation.getUserId(headers)));
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<CSRUserDTO> usersRequests = certificateRequestService.getUsersRequests(user.get());
        return new ResponseEntity<>(usersRequests,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createRequests(@Valid @RequestBody CertificateRequestDTO body, @RequestHeader Map<String, String> headers) {
        CSRUserDTO request = certificateRequestService.createRequest(body, headers);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveCSR(@RequestHeader Map<String, String> headers, @PathVariable(value = "id", required = true) @NotNull Long id){
        CSRApprovedDTO approved = certificateRequestService.approveCSR(id,userRequestValidation.getUserId(headers));
        return new ResponseEntity<>(approved,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/decline/{id}")
    public ResponseEntity<?> declineCSR(@RequestHeader Map<String, String> headers, @PathVariable(value = "id", required = true) @NotNull Integer id){
        Optional<CertificateRequest> found = certificateRequestRepository.findById(Long.valueOf(id));
        if (found.isEmpty())
            throw new CustomException("CSR with this id not found");
        CSRDeclinedDTO declined = certificateRequestService.declineCSR(found.get(),userRequestValidation.getUserId(headers));
        return new ResponseEntity<>(declined,HttpStatus.OK);
    }
}
