package com.mss.demo.config;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mss.demo.service.FileProcessingService;

@Component
public class ImportCertificateTasklet implements Tasklet {

	@Autowired
	private FileProcessingService certificateService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// In a real application, you would read data from a file or other sources.

		String[] names = { "John Doe", "Jane Doe", "Alice Smith" };
		String format = "pdf"; // Example format

		for (String name : names) {
			certificateService.saveCertificate(name, format, false, name);
		}

		return RepeatStatus.FINISHED;
	}
}
