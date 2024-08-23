package com.mss.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mss.demo.entity.Certificate;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendExpiringCertificateNotification(List<Certificate> certificates) {
	    if (certificates.isEmpty()) {
	        return;
	    }

	    for (Certificate certificate : certificates) {
	        String recipientEmail = certificate.getUserEmail(); // Use userEmail to get the recipient address

	        if (isValidEmail(recipientEmail)) {
	            StringBuilder messageBody = new StringBuilder("The following certificates are expiring soon:\n\n");
	            messageBody.append("Certificate Name: ").append(certificate.getCertificateName()).append("\n")
	                       .append("Expiry Date: ").append(certificate.getExpiryDate().toString()).append("\n\n");

	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setTo(recipientEmail);
	            message.setSubject("Expiring Certificates Notification");
	            message.setText(messageBody.toString());

	            mailSender.send(message);
	        } else {
	            System.err.println("Invalid email address: " + recipientEmail);
	        }
	    }
	}
	
	private boolean isValidEmail(String email) {
	    // Simple regex for basic email validation
	    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	    return email != null && email.matches(emailRegex);
	}
	
	public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bineetha5246@gmail.com"); // Replace with your email
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


}
