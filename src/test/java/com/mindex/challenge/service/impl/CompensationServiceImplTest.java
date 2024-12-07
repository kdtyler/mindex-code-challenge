package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
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

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;
    private String employeeUrl;

    @Autowired
    private CompensationService compensationService;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private Employee createdEmployee;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{employeeId}";
        employeeUrl = "http://localhost:" + port + "/employee";

        // Create an employee
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testEmployee.setFirstName("Kevin");
        testEmployee.setLastName("Tyler");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());
    }

    @Test
    public void testCreateCompensation() {
        // Create compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary("100000");
        testCompensation.setEffectiveDate("2023-01-01");

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class).getBody();
        assertNotNull(createdCompensation);
        assertEquals(createdEmployee.getEmployeeId(), createdCompensation.getEmployeeId());
        assertEquals("100000", createdCompensation.getSalary());
        assertEquals("2023-01-01", createdCompensation.getEffectiveDate());
    }

    @Test
    public void testReadCompensation() {
        // Create compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary("100000");
        testCompensation.setEffectiveDate("2023-01-01");

        restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class);

        // Read compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertEquals(createdEmployee.getEmployeeId(), readCompensation.getEmployeeId());
        assertEquals("100000", readCompensation.getSalary());
        assertEquals("2023-01-01", readCompensation.getEffectiveDate());
    }
}