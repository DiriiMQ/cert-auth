package com.example.signature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyConfig {

    private byte[] readPem(String path) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            pem = pem.replaceAll("-----BEGIN (.*)-----", "");
            pem = pem.replaceAll("-----END (.*)-----", "");
            pem = pem.replaceAll("\n", "");
            return Base64.getDecoder().decode(pem);
        }
    }

    @Bean
    public KeyPair keyPair() throws Exception {
        byte[] privBytes = readPem("keys/private_key.pem");
        byte[] pubBytes = readPem("keys/public_key.pem");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pubBytes));
        return new KeyPair(publicKey, privateKey);
    }
}
