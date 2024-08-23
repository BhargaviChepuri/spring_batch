package com.mss.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mss.demo.entity.Certificate;
import com.mss.demo.service.FileProcessingService;

@RestController
@RequestMapping("/api/certificates")
public class FileProcessingController {

	@Autowired
	private FileProcessingService fileProcessingService;

	@PostMapping("/add")
    public ResponseEntity<String> addCertificate(@RequestParam String name, @RequestParam String format, @RequestParam(required = false, defaultValue = "false") boolean setExpiryToMonth, @RequestParam String userEmail) {
        try {
        	fileProcessingService.saveCertificate(name, format, setExpiryToMonth,userEmail);
            return ResponseEntity.ok("Certificate created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating certificate: " + e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<List<Certificate>> getCertificatesByFileType(@RequestParam String fileType) {
        try {
            List<Certificate> certificates = fileProcessingService.getCertificatesByFileType(fileType);
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/expiring-soon")
    public List<Certificate> getExpiringSoon() {
        // This will trigger the scheduled task as well as return the data
        List<Certificate> expiringCertificates = fileProcessingService.getCertificatesExpiringSoon();
        fileProcessingService.scheduleExpiringCertificatesCheck(); // Manually trigger scheduler logic
        return expiringCertificates;
    }

}