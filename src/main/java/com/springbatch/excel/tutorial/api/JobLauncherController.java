package com.springbatch.excel.tutorial.api;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Job Launcher Controller to start jobs.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/import")
public class JobLauncherController {

    private final JobLauncherService jobLauncherService;

    /**
     * Endpoint to launch the job.
     *
     * @return ResponseEntity with HTTP Status codes
     */
    @PostMapping("/launch/csv/job")
    public ResponseEntity<String> importJson() {
        jobLauncherService.launchFileToJob(null);
        return new ResponseEntity<>("Job Executing", HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint to launch the job.
     *
     * @return ResponseEntity with HTTP Status codes
     */
    @PostMapping("/launch/csv/job2")
    public ResponseEntity<String> importJsonFromFile(@RequestBody InputFileName inputFileName) {
        jobLauncherService.launchFileToJob(inputFileName.getName());
        return new ResponseEntity<>("Job Executing", HttpStatus.ACCEPTED);
    }

    @ExceptionHandler({JobExecutionAlreadyRunningException.class, JobInstanceAlreadyCompleteException.class, JobParametersInvalidException.class, JobRestartException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("Could not initiate job due to : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
