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
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        int numberOfReports = calculateNumberOfReports(employee);
        return new ReportingStructure(employee, numberOfReports);
    }

    private int calculateNumberOfReports(Employee employee) {
        if (employee == null) {
            return 0;
        }

        System.out.println("calculateNumberOfReports on employee: " + employee.getFirstName() + " " + employee.getLastName());

        int numberOfReports = 0;
        if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()) {
            numberOfReports += employee.getDirectReports().size();
            System.out.println("numberOfReports: " + numberOfReports);
            for (Employee directReport : employee.getDirectReports()) {
                Employee fullDirectReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                System.out.println("fullDirectReport: " + fullDirectReport.getFirstName() + " " + fullDirectReport.getLastName());
                numberOfReports += calculateNumberOfReports(fullDirectReport);
            }
        }
        return numberOfReports;
    }
}