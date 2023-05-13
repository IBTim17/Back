package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.enums.UserRole;
import com.ib.Tim17_Back.exceptions.CertificateNotFoundException;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.exceptions.UserNotFoundException;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRepository;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.interfaces.ICertificateService;
import org.hibernate.Hibernate;
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
    private final JwtTokenUtil jwtTokenUtil;

    private final UserService userService;

    public CertificateService(CertificateRepository certificateRepository, JwtTokenUtil tokenUtil, UserService userService) {
        this.certificateRepository = certificateRepository;
        this.jwtTokenUtil = tokenUtil;
        this.userService = userService;
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
    public List<File> getFileBySerialNumber(String serialNumber, String token) {
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if(optionalCertificate.isEmpty()) throw new CertificateNotFoundException();

        Certificate crt = optionalCertificate.get();

        Long userId = jwtTokenUtil.getId(token);
        Optional<User> userDB = userService.findById(userId);
        if (userDB.isEmpty()) throw new UserNotFoundException();
        User user = userDB.get();

        List<File> files = new ArrayList<>();
        String fileName = "certs/" + new BigInteger(serialNumber.replace("-", ""), 16) + ".crt";
        File file = new File(fileName);
        files.add(file);
        if (crt.getOwner().equals(user) || user.getRole() == UserRole.ADMIN){
            // if owner or admin is downloading -> get public and private part of certificate
            String keyName = "keys/" + new BigInteger(serialNumber.replace("-", ""), 16) + ".key";
            file = new File(keyName);
            files.add(file);
        }
        return files;
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
