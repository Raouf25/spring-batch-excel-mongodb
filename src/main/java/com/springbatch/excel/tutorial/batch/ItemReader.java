package com.springbatch.excel.tutorial.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

public class ItemReader<T> extends FlatFileItemReader<T> {

    private final Class<T> targetType;
    private final String filePath;

    public ItemReader(String filePath, Class<T> targetType) {
        // Add null checks for constructor parameters
        if (filePath == null) {
            throw new IllegalArgumentException("File path mustn't be null.");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type mustn't be null.");
        }

        this.filePath = filePath;
        this.targetType = targetType;
    }

    public FlatFileItemReader<T> flatFileItemReader() {
        FlatFileItemReader<T> flatFileItemReader = new FlatFileItemReader<>();

        // Improve reader name for better debugging
        flatFileItemReader.setName(targetType.getSimpleName() + "Reader");

        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setResource(new ClassPathResource(filePath));
        flatFileItemReader.setLineMapper(lineMapper(targetType));
        return flatFileItemReader;
    }

    private LineMapper<T> lineMapper(Class<? extends T> targetType) {
        DefaultLineMapper<T> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        // Use constants for the magic strings
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(getFieldNames(targetType));

        defaultLineMapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(targetType);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    private String[] getFieldNames(Class<? extends T> targetType) {
        return Arrays.stream(targetType.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .filter(this::isValidFieldName)
                .map(this::capitalizeFirstLetter)
                .toList()
                .toArray(new String[0]);
    }

    private boolean isValidFieldName(String fieldName) {
        // Use a list for adding more invalid field names instead of repeating the filter function
        List<String> invalidFieldNames = List.of("serialVersionUID", "id", "integrationDateTime");

        return !invalidFieldNames.contains(fieldName);
    }

    private String capitalizeFirstLetter(String fieldName) {
        // Add a null check
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
