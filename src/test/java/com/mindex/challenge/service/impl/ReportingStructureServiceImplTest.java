package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        employeeUrl = "http://localhost:" + port + "/employee";

        // Create a test employee with direct reports
        Employee employee1 = new Employee();
//        employee1.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
//        employee1.setFirstName("John");
//        employee1.setLastName("Doe");
//        employee1.setDepartment("Engineering");
//        employee1.setPosition("Developer");

        Employee employee2 = new Employee();
//        employee2.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
//        employee2.setFirstName("Jane");
//        employee2.setLastName("Smith");
//        employee2.setDepartment("Engineering");
//        employee2.setPosition("Developer");

        Employee employee3 = new Employee();
//        employee3.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
//        employee3.setFirstName("Jim");
//        employee3.setLastName("Brown");
//        employee3.setDepartment("Engineering");
//        employee3.setPosition("Developer");

        employee1 = restTemplate.postForEntity(employeeUrl, employee1, Employee.class).getBody();
        employee2 = restTemplate.postForEntity(employeeUrl, employee2, Employee.class).getBody();
        employee3 = restTemplate.postForEntity(employeeUrl, employee3, Employee.class).getBody();

        // Keeping in for now to ensure test is similar to starting database snapshot
        employee1.setDirectReports(Arrays.asList(employee2, employee3));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee1, headers);
        restTemplate.exchange(employeeUrl, HttpMethod.PUT, request, Employee.class);
    }

    @Test
    public void testGetReportingStructureByEmployeeId() {
        Employee testEmployee = restTemplate.getForEntity(employeeUrl + "/{id}", Employee.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(testEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureByInvalidEmployeeId() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, "invalid-id");
        });

        assertEquals("Invalid employeeId: invalid-id", exception.getMessage());
    }
}