package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompensationService {
    Compensation create(String employeeId, Compensation compensation);
    Compensation read(String employeeId);
}
