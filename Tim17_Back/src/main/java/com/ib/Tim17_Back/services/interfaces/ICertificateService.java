package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ICertificateService {
    List<CertificateDTO> findAll();

    boolean isValid(String id);

    File getFileBySerialNumber(String serialNumber);
}
