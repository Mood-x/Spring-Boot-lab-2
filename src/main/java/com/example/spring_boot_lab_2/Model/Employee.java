package com.example.spring_boot_lab_2.Model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {

    @NotNull 
    @Size(min = 2, message = "ID length must be more than 2 characters")
    private String id; 

    @NotNull
    @Size(min = 4, message = "Name length must be more than 4 characters")
    @Pattern(regexp = "[a-zA-Z]*", message = "Name must contain only characters")
    private String name;

    @Email(message = "Please enter a valid email")
    private String email; 

    @Pattern(regexp = "^(05)(\\d){8}", message = "Phone number must be start with 05 and contain 10 numbers")
    private String phoneNumber; 

    @NotNull
    @Positive
    @Min(value = 25, message = "Age must be more than 25")
    private int age;

    @NotNull
    @Pattern(regexp = "^(supervisor|coordinator)$", message = "Position must be either 'supervisor', or 'coordinator'")
    private String position;

    private boolean onLeave = false;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;


    @NotNull
    @Positive(message = "Annual leave must be a positive number")
    private int annualLeave; 

}
