package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.dtos.CertificateRequestDTO;
import com.ib.Tim17_Back.enums.CertificateRequestState;
import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.exceptions.*;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRepository;
import com.ib.Tim17_Back.repositories.CertificateRequestRepository;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import com.ib.Tim17_Back.validations.UserRequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    CertificateRequestRepository requestRepository;

    @Autowired
    UserRequestValidation userRequestValidation;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Override
    public List<CSRUserDTO> usersRequests(User user) throws UsernameNotFoundException {
        List<CertificateRequest> userRequests = requestRepository.findAll();
        List<CSRUserDTO> found = new ArrayList<>();
        if (!userRequests.isEmpty()){
            for (CertificateRequest request : userRequests){
                if (request.getOwner().getEmail().equals(user.getEmail())){
                    found.add(new CSRUserDTO(request));
                }
            }
        }
        return found;
    }

    @Override
    public CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers) {
        User owner = userRepository.findById(Long.valueOf(userRequestValidation.getUserId(headers))).orElse(null);
        Long userId = Long.valueOf(userRequestValidation.getUserId(headers));
        String userRole = userRequestValidation.getRoleFromToken(headers);

        if (owner == null) throw new UserNotFoundException();

        Certificate issuerCrt;
        CertificateRequest request = new CertificateRequest();
        request.setOwner(owner);
        request.setType(body.getType());
        //TODO add organization

        if (!body.getIssuer().isEmpty()) {
            //if not creating root crt
            issuerCrt = certificateRepository.findBySerialNumber(body.getIssuer()).orElse(null);
            if (issuerCrt == null) throw new CertificateNotFoundException();

            if (issuerCrt.getType() == CertificateType.END) throw new InvalidCertificateType("Issuer cannot be END certificate");

            // TODO check if issuer has valid crt
            request.setIssuer(issuerCrt);

            if (userRole.equals("USER")) {
                // can request INTERMEDIATE or END cert
                if (body.getType() != CertificateType.ROOT || body.getType() != CertificateType.INTERMEDIATE) throw new CustomException("This user cannot request root certificate!");

                if (Objects.equals(issuerCrt.getOwner().getId(), userId)) {
                    request.setState(CertificateRequestState.ACCEPTED);
                    request = requestRepository.save(request);
                    request = acceptRequest(request, userId, userRole);
                    return new CSRUserDTO(request);
                } else {
                    request.setState(CertificateRequestState.PENDING);
                    request = requestRepository.save(request);

                    return new CSRUserDTO(request);
                }
            } else { //user is ADMIN
                request.setState(CertificateRequestState.ACCEPTED);
//                request = requestRepository.save(request);
                request = acceptRequest(request, userId, userRole);
                return new CSRUserDTO(request);
            }
        } else {
            // issuer == null / creating root crt - only if "owner" admin
            if (!userRole.equals("ADMIN")) throw new CustomException("This user cannot request root certificate!");

            request.setState(CertificateRequestState.ACCEPTED);
//            request = requestRepository.save(request);
            request = acceptRequest(request, userId, userRole);

            return new CSRUserDTO(request);
        }
    }

    private CertificateRequest acceptRequest(CertificateRequest request, Long userId, String userRole) {
        // TODO generate keypair ...
        if (request.getState() != CertificateRequestState.PENDING) throw new StatusNotPendingException();

        request.setState(CertificateRequestState.ACCEPTED);
        return requestRepository.save(request);
    }

}
