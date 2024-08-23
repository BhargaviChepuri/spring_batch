package com.mss.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mss.demo.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

	List<Certificate> findByFileType(String lowerCase);

	@Query("SELECT c FROM Certificate c WHERE c.expiryDate < :expiryDate")
	List<Certificate> findCertificatesExpiringBefore(@Param("expiryDate") LocalDate expiryDate);

	List<Certificate> findByExpiryDateBetween(LocalDate today, LocalDate oneWeekLater);

}
