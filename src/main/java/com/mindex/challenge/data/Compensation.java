package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;


public class Compensation {
    @NotBlank(message = "Salary cannot be null or empty")
    private String salary;
    @NotBlank(message = "Effective Date cannot be null or empty")
    private String effectiveDate;
    //private Employee employee;

    @NotBlank(message = "Employee ID cannot be null or empty")
    private String employeeId;

    public Compensation() {

    }

    public Compensation(String employeeId, String salary, String effectiveDate) {
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

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
