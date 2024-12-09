package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exceptionhandling.EmployeeNotFoundException;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.EMPLOYEE_NOT_FOUND;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure getReportingStructureByEmployeeId(String employeeId) {
        LOG.debug("Getting reporting structure for employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeIdAndIsDeletedFalse(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + employeeId);
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(calculateNumberOfReports(employee));

        return reportingStructure;
    }

    /*
     * Recursively calls calculateNumberOfReports to get the total number of reports for an employee
     * Will not count solf-deleted employees. Currently, a "chain" of reports can be broken
     * if there is a soft-deleted employee in the chain. The count will only include employees directly in
     * the tree with the starting node of the passed in employee.
     */
    private int calculateNumberOfReports(Employee employee) {
        if (employee == null) {
            return 0;
        }

        int numberOfReports = 0;
        if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()) {

            List<Employee> activeDirectReports = employee.getDirectReports().stream()
                    .map(directReport -> employeeRepository.findByEmployeeIdAndIsDeletedFalse(directReport.getEmployeeId()))
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            numberOfReports += activeDirectReports.size();

            for (Employee directReport : activeDirectReports) {
                Employee checkedDirectReport = employeeRepository.findByEmployeeIdAndIsDeletedFalse(directReport.getEmployeeId());
                numberOfReports += calculateNumberOfReports(checkedDirectReport);
            }
        }
        return numberOfReports;
    }
}