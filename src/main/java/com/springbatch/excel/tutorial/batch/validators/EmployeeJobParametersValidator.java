package com.springbatch.excel.tutorial.batch.validators;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.util.Map;

public class EmployeeJobParametersValidator implements JobParametersValidator {
    private static final String EXPECTED_PARAMETER_NAME = "csvFilePath";
    private static final String ERROR_MESSAGE_PARAMETER_MISSING = "Missing job parameter: ";
    private static final String ERROR_MESSAGE_PARAMETER_EMPTY = "Empty value for job parameter ";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (parameters == null) throw new AssertionError();
        Map<String, JobParameter<?>> parametersMap = parameters.getParameters();

        validatePresenceOfParameter(parametersMap);
        validateParameterValue(parametersMap);
    }

    private void validatePresenceOfParameter(Map<String, JobParameter<?>> parametersMap) throws JobParametersInvalidException {
        if (!parametersMap.containsKey(EmployeeJobParametersValidator.EXPECTED_PARAMETER_NAME)) {
            throw new JobParametersInvalidException(ERROR_MESSAGE_PARAMETER_MISSING + EmployeeJobParametersValidator.EXPECTED_PARAMETER_NAME);
        }
    }

    private void validateParameterValue(Map<String, JobParameter<?>> parametersMap) throws JobParametersInvalidException {
        String parameterValue = parametersMap.get(EmployeeJobParametersValidator.EXPECTED_PARAMETER_NAME).getValue().toString();
        if (!StringUtils.hasText(parameterValue)) {
            throw new JobParametersInvalidException(ERROR_MESSAGE_PARAMETER_EMPTY + EmployeeJobParametersValidator.EXPECTED_PARAMETER_NAME);
        }
    }
}
