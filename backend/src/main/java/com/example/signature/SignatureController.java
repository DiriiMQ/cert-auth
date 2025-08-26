package com.example.signature;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SignatureController {

    private final SignatureService service;

    public SignatureController(SignatureService service) {
        this.service = service;
    }

    private String format(String filename) {
        int i = filename.lastIndexOf('.');
        if (i < 0) throw new IllegalArgumentException("No extension");
        return filename.substring(i + 1);
    }

    @PostMapping("/sign")
    public ResponseEntity<byte[]> sign(@RequestParam("file") MultipartFile file) throws Exception {
        String fmt = format(file.getOriginalFilename());
        byte[] signed = service.sign(file.getBytes(), fmt);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename())
                .body(signed);
    }

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestParam("file") MultipartFile file) throws Exception {
        String fmt = format(file.getOriginalFilename());
        boolean valid = service.verify(file.getBytes(), fmt);
        return Map.of("valid", valid);
    }
}
