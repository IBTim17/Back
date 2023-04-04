package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CSRApprovedDTO;
import com.ib.Tim17_Back.dtos.CSRDeclinedDTO;
import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.CertificateRequestRepository;
import com.ib.Tim17_Back.services.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    CertificateRequestRepository requestRepository;

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
    public CSRDeclinedDTO declineCSR(CertificateRequest request) {
        return null;
    }

    @Override
    public CSRApprovedDTO approveCSR(CertificateRequest request) {
        return null;
    }

}
