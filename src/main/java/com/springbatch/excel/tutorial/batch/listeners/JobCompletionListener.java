package com.springbatch.excel.tutorial.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {

        String jobId = Objects.requireNonNull(jobExecution.getJobParameters().getParameter("jobId")).toString();
        String csvFilePath = jobExecution.getJobParameters().getString("csvFilePath");
        String currentTime = Objects.requireNonNull(jobExecution.getJobParameters().getDate("currentTime")).toString();


        int numDeletedLines = jobExecution.getExecutionContext().getInt("numDeletedLines", 0);
        int totalWriteCount = jobExecution.getExecutionContext().getInt("writeCount", 0);

        // get job's start time
        LocalDateTime start = jobExecution.getCreateTime();
        //  get job's end time
        LocalDateTime end = jobExecution.getEndTime();

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("==========    JOB FINISHED    =======");
            log.info("JobId: {}", jobId);
            log.info("csv Path: {}", csvFilePath);
            log.info("current Time: {}", currentTime);
            log.info("Start Date: {}", start);
            log.info("End Date: {}", end);

            log.info("Total objects written: {}", totalWriteCount);
            log.info("Total objects deleted: {}", numDeletedLines);
            log.info("delta: {}{}", (totalWriteCount - numDeletedLines > 0 ? "+" : ""), (totalWriteCount - numDeletedLines));
            log.info("=======================================");
        }

    }

}
