package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.*;

import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface ICertificateRequestService {

    CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers);
    public MessageResponseDTO declineCSR(Long csrId, Long userId, RejectionDTO reason);
    public CSRApprovedDTO approveCSR(Long csrId, Long userId);
    List<CSRUserDTO> getUsersRequests(User user) throws UsernameNotFoundException;
}
