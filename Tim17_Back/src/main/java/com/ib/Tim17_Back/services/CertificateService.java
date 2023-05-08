package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.exceptions.CertificateNotFoundException;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.repositories.CertificateRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateService;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Override
    public List<CertificateDTO> findAll() {
        List<CertificateDTO> certificates = new ArrayList<>();
        for(Certificate certificate: this.certificateRepository.findAll())
        {
            Hibernate.initialize(certificate);
            certificates.add(new CertificateDTO(certificate));
        }
        return certificates;
    }

    @Override
    public boolean isValid(String serialNumber) {
        Certificate certificate = null;
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if(optionalCertificate.isPresent()) certificate = optionalCertificate.get();
        else throw new CustomException("Certificate not found");
        Hibernate.initialize(certificate);
        if(!certificate.isValid()) return false;
        if(certificate.getEndDate().isBefore(LocalDateTime.now()))
        {
            certificate.setValid(false);
            certificateRepository.save(certificate);
            return false;
        }
        if(certificate.getStartDate().isAfter(LocalDateTime.now()))
        {
            certificate.setValid(false);
            certificateRepository.save(certificate);
            return false;
        }
        if(certificate.getIssuer() != null)
            if(!this.isValid(certificate.getIssuer().getSerialNumber())) {
                certificate.setValid(false);
                certificateRepository.save(certificate);
                return false;
            }
        return true;
    }

    @Override
    public File getFileBySerialNumber(String serialNumber) {
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if(optionalCertificate.isEmpty()) throw new CertificateNotFoundException();

        String fileName = "certs/" + new BigInteger(serialNumber.replace("-", ""), 16) + ".crt";

        return new File(fileName);
    }

    @Override
    public void revoke(String serialNumber) {
        Certificate certificate = null;
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if(optionalCertificate.isPresent()) certificate = optionalCertificate.get();
        else throw new CustomException("Certificate not found");
        Hibernate.initialize(certificate);
        List<Certificate> children = certificateRepository.findAllByIssuerIs(serialNumber);
        for (Certificate crt: children) {
            crt.setValid(false);
            certificateRepository.save(certificate);
            revoke(crt.getSerialNumber());
        }
    }
}
