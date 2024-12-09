package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/compensation/{employeeId}")
    public Compensation create(@PathVariable String employeeId, @Valid @RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employeeId [{}] and compensation [{}]", employeeId, compensation);

        return compensationService.create(employeeId, compensation);
    }

    @GetMapping("/compensation/{employeeId}")
    public Compensation read(@PathVariable String employeeId) {
        LOG.debug("Received compensation read request for employeeId [{}]", employeeId);

        return compensationService.read(employeeId);
    }
}
