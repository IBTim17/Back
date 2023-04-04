package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface ICertificateRequestService {
    List<CSRUserDTO> getUsersRequests(User user) throws UsernameNotFoundException;
}
