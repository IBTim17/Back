package com.ib.Tim17_Back.repositories;

import com.ib.Tim17_Back.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>{

}
