package com.example.signature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignatureControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private byte[] sample(String format) throws Exception {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, format, baos);
        return baos.toByteArray();
    }

    @Test
    void roundTrip() throws Exception {
        byte[] img = sample("png");
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", img);
        byte[] signed = mockMvc.perform(multipart("/api/v1/sign").file(file))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        MockMultipartFile signedFile = new MockMultipartFile("file", "img.png", "image/png", signed);
        mockMvc.perform(multipart("/api/v1/verify").file(signedFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}
