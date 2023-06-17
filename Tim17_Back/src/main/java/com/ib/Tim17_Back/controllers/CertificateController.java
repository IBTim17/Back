package com.ib.Tim17_Back.controllers;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.dtos.RevokeRequestDTO;
import com.ib.Tim17_Back.models.ErrorResponseMessage;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.interfaces.ICertificateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.mail.Multipart;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@CrossOrigin
@RequestMapping("api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;
    private static final Logger logger = LogManager.getLogger(CertificateController.class);

    private JwtTokenUtil jwtTokenUtil;

    public CertificateController(ICertificateService certificateService, JwtTokenUtil tokenUtil) {
        this.certificateService = certificateService;
        this.jwtTokenUtil = tokenUtil;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<CertificateDTO>> getAll(@RequestHeader("x-auth-token") String token) {
        logger.info("User with ID:{} requested all certificates", jwtTokenUtil.getId(token));
        List<CertificateDTO> certificates = this.certificateService.findAll();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    @GetMapping("/valid/{serialNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") //TODO test
    public ResponseEntity<Boolean> validate(@PathVariable String serialNumber, @RequestHeader("x-auth-token") String token)
    {
        logger.info("User with ID:{} requested certificate validation", jwtTokenUtil.getId(token));
        return new ResponseEntity<>(this.certificateService.isValid(serialNumber), HttpStatus.OK);
    }

    @GetMapping(value = "/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id, @RequestHeader("x-auth-token") String token, HttpServletResponse response) {
        List<File> files = certificateService.getFileBySerialNumber(id, token);
        logger.info("User with ID:{} requested to download certificate", jwtTokenUtil.getId(token));
        if (files.size() == 1) {
            InputStream inputStream = null;
            File file = files.get(0);
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.info("User with ID:{} requested to download nonexistent certificate", jwtTokenUtil.getId(token));
                return null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        }
        response.setContentType("application/zip"); // zip archive format
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename("download.zip", StandardCharsets.UTF_8)
                .build()
                .toString());


        // Archiving multiple files and responding to the client
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
            for (File file : files) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                        StreamUtils.copy(inputStream, zipOutputStream);
                    zipOutputStream.flush();
                }
            }
        } catch (IOException e) {
            logger.info("User with ID:{} failed to download zip of certificate and key", jwtTokenUtil.getId(token));
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/revoke/{serialNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> revoke(@PathVariable String serialNumber, @RequestHeader("x-auth-token") String token, @RequestBody RevokeRequestDTO reason)
    {
        logger.info("User with ID:{} requested to revoke certificate", jwtTokenUtil.getId(token));
        this.certificateService.revoke(serialNumber, token, reason);
        logger.info("User with ID:{} successfully revoked certificate", jwtTokenUtil.getId(token));
        return new ResponseEntity<>(new ErrorResponseMessage("Certificate is revoked successfully!"), HttpStatus.OK);
    }

    @PostMapping("/isvalidcert")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Boolean> isValidCert(@RequestParam MultipartFile file){
        return new ResponseEntity<>(this.certificateService.convertMultipartFileToCert(file),HttpStatus.OK);
    }


}
