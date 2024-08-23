package com.mss.demo.concrete;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;

import com.mss.demo.entity.Certificate;

@Component
public class CsvCertificateGenerator implements CertificateGenerator {

	@Override
	public String getFileExtension() {
		return "csv";
	}

	@Override
	public byte[] generateCertificate(Certificate certificate) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);

		writer.println("Certificate Name,Create Date,Expiry Date");
		writer.printf("%s,%s,%s%n", certificate.getCertificateName(), certificate.getCreateDate(),
				certificate.getExpiryDate());
		writer.flush();

		return outputStream.toByteArray();
	}
}