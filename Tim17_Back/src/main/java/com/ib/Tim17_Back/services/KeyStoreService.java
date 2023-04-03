package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.models.data.IssuerData;
import com.ib.Tim17_Back.security.keystore.KeyStoreUtil;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class KeyStoreService {

}
