package com.mss.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Lob;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String certificateName;
    private LocalDate createDate;
    private LocalDate expiryDate;

    @Lob
    private byte[] fileData;

    private String fileType;
    private String userEmail; // Added field for user email

    // Default constructor
    public Certificate() {
    }

    // Constructor with all fields except id (for creation purposes)
    public Certificate(String certificateName, LocalDate createDate, LocalDate expiryDate, byte[] fileData, String fileType, String userEmail) {
        this.certificateName = certificateName;
        this.createDate = createDate;
        this.expiryDate = expiryDate;
        this.fileData = fileData;
        this.fileType = fileType;
        this.userEmail = userEmail;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
