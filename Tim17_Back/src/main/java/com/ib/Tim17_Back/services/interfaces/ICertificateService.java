package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.dtos.RevokeRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface ICertificateService {
    List<CertificateDTO> findAll();

    boolean isValid(String id);

    List<File> getFileBySerialNumber(String serialNumber, String token);

    void revoke(String serialNumber, String token, RevokeRequestDTO reason);
    Boolean convertMultipartFileToCert(MultipartFile file);
}
