package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.services.interfaces.ICertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;

    public CertificateController(ICertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public ResponseEntity<List<CertificateDTO>> getAll() {
        List<CertificateDTO> certificates = this.certificateService.findAll();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }
}
