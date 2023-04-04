package com.ib.Tim17_Back.services.interfaces;

import com.ib.Tim17_Back.dtos.CSRApprovedDTO;
import com.ib.Tim17_Back.dtos.CSRDeclinedDTO;
import com.ib.Tim17_Back.dtos.CSRUserDTO;
import com.ib.Tim17_Back.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface ICertificateRequestService {

    public List<CSRUserDTO> usersRequests(User user) throws UsernameNotFoundException;
    public CSRDeclinedDTO declineCSR(CertificateRequest request);
    public CSRApprovedDTO approveCSR(CertificateRequest request);
    List<CSRUserDTO> getUsersRequests(User user) throws UsernameNotFoundException;

}
