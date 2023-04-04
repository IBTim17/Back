package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.security.keystore.KeyStoreUtil;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateGenerator {

    public void generateRootCertificate(CertificateDTO certificateDTO) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore keyStore = KeyStoreUtil.loadKeyStore(null, null);
        this.validate(certificateDTO);
    }

    private void validate(CertificateDTO certificateDTO) {
    }

    public void generateSelfSignedCertificate(){

    }

    public void generateCertificate(){

    }


}
