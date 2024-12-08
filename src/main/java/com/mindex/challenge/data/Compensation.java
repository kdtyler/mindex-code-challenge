package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.Date;


public class Compensation {
    @NotNull(message = "Salary cannot be null")
    @PositiveOrZero(message = "Salary must be positive or zero")
    private int salary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    //private Employee employee;

    private String employeeId;

    public Compensation() {

    }

    public Compensation(String employeeId, int salary, LocalDate effectiveDate) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
//        this.employee = employee;
//        this.employeeId = employee.getEmployeeId();
        this.employeeId = employeeId;
    }

//    public Employee getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
