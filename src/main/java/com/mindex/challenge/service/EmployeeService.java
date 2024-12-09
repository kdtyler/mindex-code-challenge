package com.mindex.challenge.service;

import com.mindex.challenge.data.Employee;
import org.springframework.validation.annotation.Validated;

@Validated
public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    void delete(String id);
}
