package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.exceptionhandling.CompensationAlreadyExistsException;
import com.mindex.challenge.exceptionhandling.CompensationNotFoundException;
import com.mindex.challenge.exceptionhandling.EmployeeNotFoundException;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.*;

@Service
@Validated
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;
    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public Compensation create(String employeeId, Compensation compensation) {
        LOG.debug("Creating compensation for employee with id [{}]", employeeId);

        // Will only create a compensation if the employee is not soft-deleted
        Employee employee = employeeRepository.findByEmployeeIdAndIsDeletedFalse(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + employeeId);
        }

        // Check if a compensation already exists for the employee
        Compensation existingCompensation = compensationRepository.findByEmployeeId(employeeId);
        if (existingCompensation != null) {
            throw new CompensationAlreadyExistsException(COMPENSATION_ALREADY_EXISTS + employeeId);
        }

        // Create new compensation
        compensation.setEmployeeId(employeeId);
        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation with employeeId [{}]", employeeId);

        // Check if employee is soft-deleted
        Employee employee = employeeRepository.findByEmployeeIdAndIsDeletedFalse(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + employeeId);
        }

        Compensation compensation = compensationRepository.findByEmployeeId(employeeId);
        if (compensation == null) {
            throw new CompensationNotFoundException(COMPENSATION_NOT_FOUND + employeeId);
        }
        return compensation;
    }
}
