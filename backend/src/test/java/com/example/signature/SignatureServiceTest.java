package com.example.signature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SignatureServiceTest {

    @Autowired
    SignatureService service;

    private byte[] sample(String format) throws Exception {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, format, baos);
        return baos.toByteArray();
    }

    @Test
    void signVerifyJpeg() throws Exception {
        byte[] img = sample("jpg");
        byte[] signed = service.sign(img, "jpg");
        assertTrue(service.verify(signed, "jpg"));
    }

    @Test
    void signVerifyPng() throws Exception {
        byte[] img = sample("png");
        byte[] signed = service.sign(img, "png");
        assertTrue(service.verify(signed, "png"));
    }
}
