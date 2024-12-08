package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }


    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    private boolean isEqualEmployees(Employee employee1, Employee employee2) {
        return employee1.getFirstName().equals(employee2.getFirstName()) &&
                employee1.getLastName().equals(employee2.getLastName()) &&
                employee1.getPosition().equals(employee2.getPosition()) &&
                employee1.getDepartment().equals(employee2.getDepartment()) &&
                employee1.getDirectReports().equals(employee2.getDirectReports());
    }

    /*
    @Override
    public Employee update(Employee employee) {
    LOG.debug("Updating employee [{}]", employee);

    Employee existingEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());
    if (existingEmployee == null) {
        throw new RuntimeException("Employee not found with id: " + employee.getEmployeeId());
    }

    if (employee.getFirstName() != null) {
        existingEmployee.setFirstName(employee.getFirstName());
    }
    if (employee.getLastName() != null) {
        existingEmployee.setLastName(employee.getLastName());
    }
    if (employee.getPosition() != null) {
        existingEmployee.setPosition(employee.getPosition());
    }
    if (employee.getDepartment() != null) {
        existingEmployee.setDepartment(employee.getDepartment());
    }
    if (employee.getDirectReports() != null) {
        existingEmployee.setDirectReports(employee.getDirectReports());
    }


    return employeeRepository.save(existingEmployee);
    }

     */
}
