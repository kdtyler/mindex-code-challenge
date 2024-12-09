package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.exceptionhandling.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.EMPLOYEE_NOT_FOUND;

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

        Employee employee = employeeRepository.findByEmployeeIdAndIsDeletedFalse(id);

        if (employee == null) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + id);
        }

        return employee;
    }

    // Currently acts purely as an update, not an updateOrCreate. Will not update soft-deleted employees
    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        Employee existingEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());

        if (existingEmployee == null || existingEmployee.getIsDeleted()) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + employee.getEmployeeId());
        }

        return employeeRepository.save(employee);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Soft deleting employee with id [{}]", id);

        Employee employee = read(id);
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }
}
