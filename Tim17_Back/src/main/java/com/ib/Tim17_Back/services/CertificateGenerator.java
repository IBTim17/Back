package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.CertificateDTO;
import com.ib.Tim17_Back.enums.CertificateType;
import com.ib.Tim17_Back.models.Certificate;
import com.ib.Tim17_Back.models.CertificateRequest;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.models.data.IssuerData;
import com.ib.Tim17_Back.models.data.SubjectData;
import com.ib.Tim17_Back.repositories.CertificateRepository;
import com.ib.Tim17_Back.security.keystore.KeyStoreUtil;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class CertificateGenerator {

    @Autowired
    private CertificateRepository certificateRepository;

    public void generateRootCertificate(CertificateDTO certificateDTO) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore keyStore = KeyStoreUtil.loadKeyStore(null, null);
    }

    public void generateSelfSignedCertificate(){

    }

    public void generateCertificate(CertificateRequest request) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        KeyStore keyStore = KeyStoreUtil.loadKeyStore(null, null);
        KeyPair keyPair = generateKeyPair();

        X500Name subjectData = generateSubjectData(request);
        X500Name issuerData = generateIssuerData(request);

        LocalDateTime startDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusYears(1);
        if (request.getType().equals(CertificateType.INTERMEDIATE))
            endDate = endDate.plusYears(4);

        String serialNumber = UUID.randomUUID().toString();

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        ContentSigner contentSigner = builder.build(keyPair.getPrivate());

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                issuerData,
                new BigInteger(serialNumber),
                Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()),
                subjectData,
                keyPair.getPublic());

        X509CertificateHolder certHolder = certGen.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate certificate = certConverter.getCertificate(certHolder);

        Certificate certificateDB = new Certificate(request.getOwner(), this.certificateRepository.findBySerialNumber(request.getIssuerSN()).get(), serialNumber ,request.getType(), startDate, endDate, true, request.getOrganization());
        this.saveCertificate(certificate, certificateDB, keyPair);
    }

    private void saveCertificate(X509Certificate certificate, Certificate certificateDB, KeyPair keyPair) {
        try {
            Files.write(Paths.get("certs/", certificate.getSerialNumber() + ".crt"), certificate.getEncoded());
            Files.write(Paths.get("keys/", certificate.getSerialNumber() + ".key"), keyPair.getPrivate().getEncoded());
        } catch (IOException | CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
        this.certificateRepository.save(certificateDB);
    }


    private X500Name generateSubjectData(CertificateRequest request) {
        User user = request.getOwner();

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getFirstName() + " " + user.getLastName());
        builder.addRDN(BCStyle.SURNAME, user.getLastName());
        builder.addRDN(BCStyle.GIVENNAME, user.getFirstName());
        builder.addRDN(BCStyle.O, request.getOrganization());
        builder.addRDN(BCStyle.E, user.getEmail());
        builder.addRDN(BCStyle.UID, user.getId().toString());
        return builder.build();
    }

    private X500Name generateIssuerData(CertificateRequest request) {
        Certificate issuer = certificateRepository.findBySerialNumber(request.getIssuerSN()).orElse(null);


        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, issuer.getOwner().getFirstName() + " " + issuer.getOwner().getLastName());
        builder.addRDN(BCStyle.SURNAME, issuer.getOwner().getLastName());
        builder.addRDN(BCStyle.GIVENNAME, issuer.getOwner().getFirstName());
        builder.addRDN(BCStyle.E, issuer.getOwner().getEmail());
        builder.addRDN(BCStyle.UID, issuer.getOwner().getId().toString());
        return builder.build();
    }


    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

}
