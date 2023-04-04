package com.ib.Tim17_Back.repositories;

import com.ib.Tim17_Back.models.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

}
