package com.springbatch.excel.tutorial.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Date;

/**
 * @author aek
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobLauncherService {

    private final Job job;

    private final JobLauncher jobLauncher;

    @Value("${employee.excel.processingfolder}")
    private String processingDir;

    @Async
    public void launchFileToJob(String filename) {
        log.info("Starting job");

        String excelFilePath = Paths.get(processingDir, "employee.xlsx").toString();

        String csvFilePath = filename == null ? Paths.get(processingDir, "employee.csv").toString() : filename;

        log.info("Excel File Path: {}", excelFilePath);
        log.info("CSV File Path: {}", csvFilePath);

        JobParameters params = new JobParametersBuilder()
                .addLong("jobId", System.currentTimeMillis())
                .addDate("currentTime", new Date())
                .addString("excelPath", excelFilePath)
                .addString("csvFilePath", csvFilePath)
                .toJobParameters();

        try {
            jobLauncher.run(job, params);
            log.info("Job launched successfully");
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.info("Job launch failed with exception: {}", e.getMessage());
            throw new JobLaunchException(e.getMessage(), e);
        } finally {
            log.info("Stopping job");
        }
    }
}
