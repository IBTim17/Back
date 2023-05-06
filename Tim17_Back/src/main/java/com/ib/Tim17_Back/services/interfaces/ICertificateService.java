package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface ICertificateService {
    List<CertificateDTO> findAll();

    boolean isValid(String id);

    X509Certificate convertMultipartFileToCert(MultipartFile file);
}
