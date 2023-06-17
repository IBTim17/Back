package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.*;

import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface ICertificateRequestService {

    CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers);
    MessageResponseDTO declineCSR(Long csrId, Long userId, RejectionDTO reason);
    MessageResponseDTO approveCSR(Long csrId, Long userId);
    List<CertificateDTO> getUsersRequests(User user) throws UsernameNotFoundException;
}
