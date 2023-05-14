package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.enums.CertificateRequestState;
import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.enums.UserRole;
import com.ib.Tim17_Back.exceptions.*;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRepository;
import com.ib.Tim17_Back.repositories.CertificateRequestRepository;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import com.ib.Tim17_Back.validations.UserRequestValidation;
import org.bouncycastle.operator.OperatorCreationException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    CertificateRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    CertificateService certificateService;

    @Autowired
    UserRequestValidation userRequestValidation;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    CertificateGenerator generator;


    @Override
    public List<CSRUserDTO> getUsersRequests(User user) throws UsernameNotFoundException {
        List<CertificateRequest> userRequests = requestRepository.findAll();
        List<CSRUserDTO> found = new ArrayList<>();
        if (!userRequests.isEmpty()){
            for (CertificateRequest request : userRequests){
                if (user.getRole()==UserRole.ADMIN){
                    found.add(new CSRUserDTO(request));
                }else {
                    if (request.getOwner().getEmail().equals(user.getEmail())){
                        found.add(new CSRUserDTO(request));
                    }
                }
            }
        }
        return found;
    }

    @Override
    public CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers) {
        User owner = userRepository.findById(userRequestValidation.getUserId(headers)).orElse(null);
        Long userId = userRequestValidation.getUserId(headers);
        String userRole = userRequestValidation.getRoleFromToken(headers);

        if (owner == null) throw new UserNotFoundException();

        Certificate issuerCrt;
        CertificateRequest request = new CertificateRequest();
        request.setOwner(owner);
        request.setType(body.getType());
        request.setOrganization(body.getOrganization());

        if (body.getIssuer() != null) {
            //if not creating root crt
            issuerCrt = certificateRepository.findBySerialNumber(body.getIssuer()).orElse(null);
            if (issuerCrt == null) throw new CertificateNotFoundException();
            if(!this.certificateService.isValid(issuerCrt.getSerialNumber())) throw new CustomException("Certificate is not valid!");
            if (issuerCrt.getType() == CertificateType.END) throw new InvalidCertificateType("Issuer cannot be END certificate");

            request.setIssuerSN(issuerCrt.getSerialNumber());

            if (userRole.equals("USER")) {
                // can request INTERMEDIATE or END cert
                if (body.getType() == CertificateType.ROOT) throw new CustomException("This user cannot request root certificate!");

                if (Objects.equals(issuerCrt.getOwner().getId(), userId)) {
                    request.setState(CertificateRequestState.ACCEPTED);
                    request = requestRepository.save(request);
                    try {
                        generator.generateSelfSignedCertificate(request);
                    } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException |
                             NoSuchProviderException | OperatorCreationException e) {
                        throw new RuntimeException(e);
                    }
                    return new CSRUserDTO(request);
                } else {
                    request.setState(CertificateRequestState.PENDING);
                    request = requestRepository.save(request);
                    return new CSRUserDTO(request);
                }
            } else { //user is ADMIN
                request.setState(CertificateRequestState.ACCEPTED);
                request = requestRepository.save(request);
                try {
                    generator.generateCertificate(request);
                } catch (CertificateException | KeyStoreException | IOException | OperatorCreationException |
                         NoSuchAlgorithmException | NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }
//                approveCSR(request.getId(), userId);
                return new CSRUserDTO(request);
            }
        } else {
            // issuer == null / creating root crt - only if "owner" admin
            if (!userRole.equals("ADMIN")) throw new CustomException("This user cannot request root certificate!");

            request.setState(CertificateRequestState.ACCEPTED);
            request = requestRepository.save(request);

            try {
                generator.generateSelfSignedCertificate(request);
            } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException |
                     NoSuchProviderException | OperatorCreationException e) {
                throw new RuntimeException(e);
            }
            return new CSRUserDTO(request);
        }
    }

    @Override
    public MessageResponseDTO declineCSR(Long csrId, Long userId, RejectionDTO reasonDTO) {
        Optional<CertificateRequest> request = requestRepository.findById(csrId);
        Optional<Certificate> certificate = certificateRepository.findBySerialNumber(request.get().getIssuerSN());
        if (request.isEmpty())
            throw new CustomException("CSR with this id not found");
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty())
            throw new CustomException("User does not exist.");
        if (!certificate.get().getOwner().getEmail().equals(foundUser.get().getEmail()))
            throw new CustomException("This is not users certificate");
        request.get().setState(CertificateRequestState.DENIED);
        request.get().setRejectReason(reasonDTO.getReason());
        requestRepository.save(request.get());
        return new MessageResponseDTO("Successfully denied certificate.");
    }

    @Override
    public MessageResponseDTO approveCSR(Long csrId, Long userId) {
        Optional<CertificateRequest> request = requestRepository.findById(csrId);
        if (request.isEmpty())
            throw new CustomException("CSR with this id not found");
        Hibernate.initialize(request);
        Certificate issuer = certificateRepository.findBySerialNumber(request.get().getIssuerSN()).orElse(null);
        if (issuer==null)
            return null;
        if (!issuer.getOwner().getId().equals(userId))
            throw new CustomException("Your not allowed to approve this certificate.");
        if (!this.certificateService.isValid(issuer.getSerialNumber()))
            throw new CustomException("Issuer certificate is not valid.");
        try {
            generator.generateCertificate(request.get());
        } catch (CertificateException | KeyStoreException | IOException | OperatorCreationException |
                 NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        request.get().setState(CertificateRequestState.ACCEPTED);
        requestRepository.save(request.get());
        return new MessageResponseDTO("Certificate approved");

    }

}
