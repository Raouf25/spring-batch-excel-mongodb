package com.springbatch.excel.tutorial.batch.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;


public class CountingStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // No action needed before step
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long writeCount = stepExecution.getWriteCount();
        stepExecution.getJobExecution().getExecutionContext().putInt("writeCount", (int) writeCount);
        return null;
    }
}
