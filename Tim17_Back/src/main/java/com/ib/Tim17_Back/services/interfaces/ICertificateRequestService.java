package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.dtos.CertificateRequestDTO;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ICertificateRequestService {
    public List<CSRUserDTO> usersRequests(User user) throws UsernameNotFoundException;

    CSRUserDTO createRequest(CertificateRequestDTO body, Map<String, String> headers);
}
