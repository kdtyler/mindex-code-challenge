package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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
        LOG.debug("Creating employee with id [{}]", id);

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
    /*
    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        LOG.debug("Getting reporting structure for employee with id [{}]", employeeId);
        Employee employee = read(employeeId);
        int numberOfReports = calculateNumberOfReports(employee);
        return new ReportingStructure(employee, numberOfReports);
    }

    private int calculateNumberOfReports(Employee employee) {
        List<Employee> directReports = employee.getDirectReports();
        int count = 0;
        if (directReports != null) {
            count += directReports.size();
            for (Employee directReport : directReports) {
                count += calculateNumberOfReports(directReport);
            }
        }
        return count;
    }
    */


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
