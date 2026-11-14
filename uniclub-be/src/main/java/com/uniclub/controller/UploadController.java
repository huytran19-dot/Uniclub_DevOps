package com.uniclub.controller;

import com.uniclub.dto.response.UploadResponse;
import com.uniclub.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@PreAuthorize("hasRole('SysAdmin')")
public class UploadController {

    private final StorageService storageService;

    @Autowired
    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/image")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.uploadImage(file);
        return ResponseEntity.ok(UploadResponse.of(imageUrl, "Image uploaded successfully"));
    }

    @PostMapping("/image/variant")
    public ResponseEntity<UploadResponse> uploadVariantImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.uploadImage(file, "uniclub/variants");
        return ResponseEntity.ok(UploadResponse.of(imageUrl, "Variant image uploaded successfully"));
    }

    @DeleteMapping("/image")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestParam("url") String imageUrl) {
        storageService.deleteImage(imageUrl);
        return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
    }
}
