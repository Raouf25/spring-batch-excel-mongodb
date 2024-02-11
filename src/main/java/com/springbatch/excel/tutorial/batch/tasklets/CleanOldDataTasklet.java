package com.springbatch.excel.tutorial.batch.tasklets;

import com.mongodb.client.result.DeleteResult;
import com.springbatch.excel.tutorial.domain.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class CleanOldDataTasklet implements Tasklet {

    private static final String INTEGRATION_DATE_TIME_FIELD = "integrationDateTime";
    private final MongoTemplate mongoTemplate;
    private final Date currentTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Use slf4j logger (don't use system.out.print or equivalent)
        log.info("Deleting outdated Employee entries that do not match current time: {}", currentTime);

        try {
            int numDeletedLines = (int) deleteOutdatedItems().getDeletedCount();
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().putInt("numDeletedLines", numDeletedLines);
        } catch (Exception e) {
            log.error("Failed to delete outdated Employee entries: ", e);
            // Consider if you want the job to stop here or continue with a warning
            throw e; // or return RepeatStatus.CONTINUABLE;
        }

        return RepeatStatus.FINISHED;
    }

    private DeleteResult deleteOutdatedItems() {
        Query deleteQuery = createDeleteQuery();
        return mongoTemplate.remove(deleteQuery, Employee.class);
    }

    private Query createDeleteQuery() {
        return new Query()
                .addCriteria(Criteria.where(INTEGRATION_DATE_TIME_FIELD).ne(currentTime));
    }
}
