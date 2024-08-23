package com.mss.demo.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.mss.demo.concrete.CertificateGenerator;
import com.mss.demo.entity.Certificate;
import com.mss.demo.repository.CertificateRepository;

@Service
public class FileProcessingService {

	@Autowired
	private CertificateRepository certificateRepository;

	@Autowired
	private List<CertificateGenerator> certificateGenerators;
	
	@Autowired
	private EmailService emailService;

	public void saveCertificate(String name, String format, boolean setExpiryToMonth, String userEmail) {
	    // Validate email address
	    if (!isValidEmail(userEmail)) {
	        throw new IllegalArgumentException("Invalid email address: " + userEmail);
	    }

	    LocalDate createDate = LocalDate.now();
	    LocalDate expiryDate;

	    if (setExpiryToMonth) {
	        expiryDate = createDate.plusMonths(1);
	    } else {
	        expiryDate = createDate.plusWeeks(1);
	    }

	    CertificateGenerator generator = certificateGenerators.stream()
	            .filter(g -> g.getFileExtension().equalsIgnoreCase(format))
	            .findFirst()
	            .orElseThrow(() -> new IllegalArgumentException("Unsupported format: " + format));

	    byte[] fileData = generator.generateCertificate(new Certificate(name, createDate, expiryDate, null, format, userEmail));

	    Certificate entity = new Certificate(name, createDate, expiryDate, fileData, format, userEmail);
	    certificateRepository.save(entity);
	}

	private boolean isValidEmail(String email) {
	    // Simple regex for basic email validation
	    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	    return email != null && email.matches(emailRegex);
	}



	public List<Certificate> getCertificatesByFileType(String fileType) {
		return certificateRepository.findByFileType(fileType.toLowerCase());
	}

	// FileProcessingService.java
	public List<Certificate> getCertificatesExpiringSoon() {
	    LocalDate today = LocalDate.now();
	    LocalDate oneWeekLater = today.plusWeeks(1);
	    return certificateRepository.findByExpiryDateBetween(today, oneWeekLater);
	}

	@Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void scheduleExpiringCertificatesCheck() {
        // Check for certificates expiring in one week
        List<Certificate> weeklyExpiringCertificates = getCertificatesExpiringSoon();
        System.out.println("Scheduled check: Found " + weeklyExpiringCertificates.size() + " certificates expiring in one week.");
        emailService.sendExpiringCertificateNotification(weeklyExpiringCertificates);

//        // Check for certificates expiring in one month
//        List<Certificate> monthlyExpiringCertificates = getCertificatesExpiringSoon();
//        System.out.println("Scheduled check: Found " + monthlyExpiringCertificates.size() + " certificates expiring in one month.");
//        emailService.sendExpiringCertificateNotification(monthlyExpiringCertificates);
    }
}