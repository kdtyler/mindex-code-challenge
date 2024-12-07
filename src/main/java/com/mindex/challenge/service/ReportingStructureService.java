package com.mindex.challenge.service;

import com.mindex.challenge.data.ReportingStructure;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ReportingStructureService {
    ReportingStructure getReportingStructureByEmployeeId(String employeeId);
}
