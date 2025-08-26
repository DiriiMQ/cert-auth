package com.example.signature;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyConfig {

    private final Environment environment;

    public KeyConfig(Environment environment) {
        this.environment = environment;
    }

    private byte[] parsePemKey(String pemContent) {
        String cleaned = pemContent
                .replaceAll("-----BEGIN.*-----", "")
                .replaceAll("-----END.*-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(cleaned);
    }

    private KeyPair loadKeysFromEnvironment() throws Exception {
        String privateKeyPem = environment.getProperty("RSA_PRIVATE_KEY");
        String publicKeyPem = environment.getProperty("RSA_PUBLIC_KEY");

        if (privateKeyPem != null && publicKeyPem != null) {
            byte[] privateKeyBytes = parsePemKey(privateKeyPem);
            byte[] publicKeyBytes = parsePemKey(publicKeyPem);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            return new KeyPair(publicKey, privateKey);
        }
        return null;
    }

    private KeyPair loadKeysFromFiles() throws Exception {
        String privateKeyPath = environment.getProperty("RSA_PRIVATE_KEY_FILE");
        String publicKeyPath = environment.getProperty("RSA_PUBLIC_KEY_FILE");

        if (privateKeyPath != null && publicKeyPath != null) {
            File privateFile = new File(privateKeyPath);
            File publicFile = new File(publicKeyPath);

            if (privateFile.exists() && publicFile.exists() && 
                !privateFile.getAbsolutePath().contains("src/main/resources")) {
                
                String privateKeyPem = Files.readString(Paths.get(privateKeyPath), StandardCharsets.UTF_8);
                String publicKeyPem = Files.readString(Paths.get(publicKeyPath), StandardCharsets.UTF_8);

                byte[] privateKeyBytes = parsePemKey(privateKeyPem);
                byte[] publicKeyBytes = parsePemKey(publicKeyPem);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
                PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

                return new KeyPair(publicKey, privateKey);
            }
        }
        return null;
    }

    private KeyPair generateKeyPair() throws Exception {
        // Use Nimbus JOSE+JWT library for consistent key generation
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .generate();
        return rsaKey.toKeyPair();
    }

    @Bean
    public KeyPair keyPair() throws Exception {
        // Priority 1: Load from environment variables (production)
        KeyPair envKeys = loadKeysFromEnvironment();
        if (envKeys != null) {
            return envKeys;
        }

        // Priority 2: Load from external file paths (staging)
        KeyPair fileKeys = loadKeysFromFiles();
        if (fileKeys != null) {
            return fileKeys;
        }

        // Priority 3: Generate dynamically (development/testing)
        return generateKeyPair();
    }
}
