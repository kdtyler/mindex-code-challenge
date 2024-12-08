package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure getReportingStructureByEmployeeId(String employeeId) {
        LOG.debug("Getting reporting structure for employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(calculateNumberOfReports(employee));

        return reportingStructure;
    }

    // Recursively calls calculateNumberOfReports to get the total number of reports for an employee
    private int calculateNumberOfReports(Employee employee) {
        if (employee == null) {
            return 0;
        }

        int numberOfReports = 0;
        if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()) {
            numberOfReports += employee.getDirectReports().size();

            for (Employee directReport : employee.getDirectReports()) {
                Employee fullDirectReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                numberOfReports += calculateNumberOfReports(fullDirectReport);
            }
        }
        return numberOfReports;
    }
}