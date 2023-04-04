package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CSRApprovedDTO;
import com.ib.Tim17_Back.dtos.CSRDeclinedDTO;
import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.dtos.CertificateRequestDTO;

import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface ICertificateRequestService {

    CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers);
    public CSRDeclinedDTO declineCSR(CertificateRequest request,Long userId);
    public CSRApprovedDTO approveCSR(Integer csrId,Long userId);

    List<CSRUserDTO> getUsersRequests(User user) throws UsernameNotFoundException;
}
