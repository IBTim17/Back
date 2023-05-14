package com.ib.Tim17_Back.repositories;

import com.ib.Tim17_Back.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>{

    Optional<Certificate> findBySerialNumber(String serialNumber);

    List<Certificate> findAllByIssuer_SerialNumber(String serialNumber);

}
