package com.ib.Tim17_Back.security.keystore;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Vector;

public class CertificateUtil {

    public static X500Name createSubjectX500Name(X500Name subject, String caSerialNumber) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, subject.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.O, subject.getRDNs(BCStyle.O)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.OU, subject.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.L, subject.getRDNs(BCStyle.L)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.ST, subject.getRDNs(BCStyle.ST)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.SERIALNUMBER, caSerialNumber);
        builder.addRDN(BCStyle.C, subject.getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.EmailAddress, subject.getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString());
        return builder.build();
    }

    public static void addBasicConstraints(X509v3CertificateBuilder certGen, boolean isCA) throws CertIOException {
        certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
    }

    public static void addKeyUsage(X509v3CertificateBuilder certGen, ArrayList<String> usages) throws Exception {
        ArrayList<String> allowed = new ArrayList<String>( ){
            private static final long serialVersionUID = 1L;
            {
                add("digitalSignature");
                add("nonRepudiation");
                add("keyEncipherment");
                add("dataEncipherment");
                add("keyAgreement");
                add("encipherOnly");
                add("decipherOnly");
            } };
        boolean valid = true;
        for (String u : usages) {
            if (!allowed.contains(u)) {
                valid = false;
            }
        }
        if (usages.size() == 0 ) {
            certGen.addExtension(Extension.keyUsage, true, new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.keyEncipherment));
            return;
        }
        int keyUsage = 0;
        if (valid) {
            if (usages.contains("digitalSignature")) {
                keyUsage = keyUsage | X509KeyUsage.digitalSignature;
            }
            if (usages.contains("nonRepudiation")) {
                keyUsage = keyUsage | X509KeyUsage.nonRepudiation;
            }
            if (usages.contains("keyEncipherment")) {
                keyUsage = keyUsage | X509KeyUsage.keyEncipherment;
            }
            if (usages.contains("dataEncipherment")) {
                keyUsage = keyUsage | X509KeyUsage.dataEncipherment;
            }
            if (usages.contains("keyAgreement")) {
                keyUsage = keyUsage | X509KeyUsage.keyAgreement;
            }
            if (usages.contains("encipherOnly")) {
                keyUsage = keyUsage | X509KeyUsage.encipherOnly;
            }
            if (usages.contains("decipherOnly")) {
                keyUsage = keyUsage | X509KeyUsage.decipherOnly;
            }
            certGen.addExtension(Extension.keyUsage, true, new X509KeyUsage(keyUsage));
        } else {
            throw new Exception("Invalid key usage!");
        }
    }

    public static void addCAKeyUsage(X509v3CertificateBuilder certGen) throws Exception {
        certGen.addExtension(Extension.keyUsage, true, new X509KeyUsage(X509KeyUsage.keyCertSign));
    }

    @SuppressWarnings("deprecation")
    public static void addExtendedKeyUsage(X509v3CertificateBuilder certGen, ArrayList<String> usages) throws Exception {
        ArrayList<String> allowed = new ArrayList<String>(){
            private static final long serialVersionUID = 1L;

            {
                add("serverAuth");
                add("clientAuth");
                add("codeSigning");
                add("emailProtection");
                add("timeStamping");
            } };
        boolean valid = true;
        for (String u : usages) {
            if (!allowed.contains(u)) {
                valid = false;
            }
        }
        Vector<KeyPurposeId> extendedKeyUsages = new Vector<>();
        if (usages.size() == 0) {
            extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
            certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(extendedKeyUsages));
            return;
        }
        if (valid) {
            if (usages.contains("serverAuth")) {
                extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
            }
            if (usages.contains("clientAuth")) {
                extendedKeyUsages.add(KeyPurposeId.id_kp_clientAuth);
            }
            if (usages.contains("codeSigning")) {
                extendedKeyUsages.add(KeyPurposeId.id_kp_codeSigning);
            }
            if (usages.contains("emailProtection")) {
                extendedKeyUsages.add(KeyPurposeId.id_kp_emailProtection);
            }
            if (usages.contains("timeStamping")) {
                extendedKeyUsages.add(KeyPurposeId.id_kp_timeStamping);
            }
            certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(extendedKeyUsages));
        } else {
            throw new Exception("Invalid extended key usage!");
        }
    }

    public static void addAlternativeNamesExtensions(X509v3CertificateBuilder certGen, String subjectEmail, String issuerEmail) throws CertIOException {
        ArrayList<GeneralName> generalNames = new ArrayList<>();
        generalNames.add(new GeneralName(GeneralName.rfc822Name, subjectEmail));
        generalNames.add(new GeneralName(GeneralName.dNSName, "localhost"));
        GeneralNames altNamesSeq = GeneralNames.getInstance(new DERSequence(generalNames.toArray(new GeneralName[]{})));
        certGen.addExtension(Extension.subjectAlternativeName, false, altNamesSeq);

        generalNames.clear();
        generalNames.add(new GeneralName(GeneralName.rfc822Name, issuerEmail));
        generalNames.add(new GeneralName(GeneralName.dNSName, "localhost"));
        altNamesSeq = GeneralNames.getInstance(new DERSequence(generalNames.toArray(new GeneralName[]{})));
        certGen.addExtension(Extension.issuerAlternativeName, false, altNamesSeq);
    }

    public static void addKeyIdentifierExtensions(X509v3CertificateBuilder certGen, PublicKey subjectKey, PublicKey issuerKey) throws IOException, NoSuchAlgorithmException {
        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
        certGen.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils.createSubjectKeyIdentifier(subjectKey));
        certGen.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(issuerKey));
    }

    public static File x509CertificateToPem(X509Certificate cert, String path, String name) throws IOException {
        File f = new File(path + name + ".crt");
        FileWriter fileWriter = new FileWriter(f);
        JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        return f;
    }

    public static X500Name createX500Name(String commonName, String organization, String organizationalUnit, String cityLocality, String stateCountyRegion, String country, String emailAddress) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, commonName);
        builder.addRDN(BCStyle.O, organization);
        builder.addRDN(BCStyle.OU, organizationalUnit);
        builder.addRDN(BCStyle.L, cityLocality);
        builder.addRDN(BCStyle.ST, stateCountyRegion);
        builder.addRDN(BCStyle.C, country);
        builder.addRDN(BCStyle.EmailAddress, emailAddress);

        return builder.build();
    }

    public static void createPKCS12(BigInteger serialNumber, PrivateKey privateKey, X509Certificate newCertificate) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        pkcs12.setKeyEntry("privatekeyalias", privateKey, "entrypassphrase".toCharArray(), new Certificate[] {newCertificate});
        try (FileOutputStream p12 = new FileOutputStream(serialNumber + ".p12")) {
            pkcs12.store(p12, "p12passphrase".toCharArray());
        }
    }

}
