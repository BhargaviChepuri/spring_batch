package com.mss.demo.concrete;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mss.demo.entity.Certificate;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

@Component
public class PdfCertificateGenerator implements CertificateGenerator {

	@Override
	public String getFileExtension() {
		return "pdf";
	}

	@Override
	public byte[] generateCertificate(Certificate certificate) {
		Document document = new Document();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();
			document.add(new Paragraph("Certificate Name: " + certificate.getCertificateName()));
			document.add(new Paragraph("Create Date: " + certificate.getCreateDate()));
			document.add(new Paragraph("Expiry Date: " + certificate.getExpiryDate()));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}
}
