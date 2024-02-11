package com.springbatch.excel.tutorial.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee")
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String number;
    private String email;
    private String department;
    private double salary;
    @Indexed
    private Date integrationDateTime;
}
