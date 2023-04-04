package com.ib.Tim17_Back.security.keystore;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public class KeyStoreUtil {

    public static KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
        keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        return keyStore;
    }

    public static void saveKeyStore(KeyStore keyStore, String path, String pass) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            keyStore.store(fos, pass.toCharArray());
        }
    }

    public static void deleteEntry(KeyStore ks, String alias) throws KeyStoreException {
        ks.deleteEntry(alias);
    }
}
