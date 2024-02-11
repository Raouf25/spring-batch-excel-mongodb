package com.springbatch.excel.tutorial.batch;

import com.springbatch.excel.tutorial.batch.listeners.CountingStepExecutionListener;
import com.springbatch.excel.tutorial.batch.listeners.JobCompletionListener;
import com.springbatch.excel.tutorial.batch.tasklets.CleanOldDataTasklet;
import com.springbatch.excel.tutorial.batch.validators.EmployeeJobParametersValidator;
import com.springbatch.excel.tutorial.domain.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.List;

/**
 * Configuration for batch
 */
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    public final JobRepository jobRepository;
    public final MongoTemplate mongoTemplate;
    public final PlatformTransactionManager platformTransactionManager;


    @Bean
    public JobParametersValidator compositeJobParametersValidator() {
        CompositeJobParametersValidator bean = new CompositeJobParametersValidator();
        bean.setValidators(List.of(new EmployeeJobParametersValidator()));
        return bean;
    }


    @Bean
    @StepScope
    public ItemProcessor<Employee, Employee> itemProcessor(@Value("#{jobParameters['currentTime']}") Date currentTime) {
        return item -> {
            item.setIntegrationDateTime(currentTime);
            return item;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> itemReader(@Value("#{jobParameters['csvFilePath']}") String filePath) {
        return new ItemReader<>(filePath, Employee.class).flatFileItemReader();
    }


    @Bean
    public MongoItemWriter<Employee> writerMongo() {
        return new MongoItemWriterBuilder<Employee>()
                .template(mongoTemplate)
                .collection("employee")
                .build();
    }

    @Bean
    @JobScope
    public CleanOldDataTasklet cleanOldDataTasklet(@Value("#{jobParameters['currentTime']}") Date currentTime) {
        return new CleanOldDataTasklet(mongoTemplate, currentTime);
    }

    @Bean
    public Step deleteOldEmployeesStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteOldCouponOffersStep", jobRepository)
                .tasklet(cleanOldDataTasklet(null), transactionManager)
                .build();
    }


    @Bean
    public Step employeeStep() {
        return new StepBuilder("employeeStep", jobRepository)
                .<Employee, Employee>chunk(150, platformTransactionManager)
                .reader(itemReader(null))
                .processor(itemProcessor(null))
                .writer(writerMongo())
                .listener(countingStepExecutionListener())
                .build();
    }

    @Bean
    public CountingStepExecutionListener countingStepExecutionListener() {
        return new CountingStepExecutionListener();
    }

    @Bean
    public JobCompletionListener jobCompletionListener() {
        return new JobCompletionListener();
    }

    @Bean
    public Job employeeJob() {
        return new JobBuilder("employeeJob", jobRepository)
                .validator(compositeJobParametersValidator())
                .listener(jobCompletionListener())
                .incrementer(new RunIdIncrementer())
                .flow(deleteOldEmployeesStep(platformTransactionManager))
                .next(employeeStep())
                .end()
                .build();
    }

}
