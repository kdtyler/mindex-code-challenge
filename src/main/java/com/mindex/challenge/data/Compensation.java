package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Compensation {
    private String salary;
    private String effectiveDate;
    //private Employee employee;

    private String employeeId;

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

    public void getEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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
