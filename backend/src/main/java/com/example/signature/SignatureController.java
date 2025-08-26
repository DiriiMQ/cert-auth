package com.example.signature;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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
    public ResponseEntity<?> sign(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing or empty file parameter"));
            }
            
            String fmt = format(file.getOriginalFilename());
            byte[] signed = service.sign(file.getBytes(), fmt);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename())
                    .body(signed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to sign image"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing or empty file parameter"));
            }
            
            String fmt = format(file.getOriginalFilename());
            boolean valid = service.verify(file.getBytes(), fmt);
            
            return ResponseEntity.ok(Map.of("valid", valid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
