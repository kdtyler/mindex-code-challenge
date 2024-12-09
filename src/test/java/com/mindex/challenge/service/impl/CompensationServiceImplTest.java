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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.EMPLOYEE_NOT_FOUND;
import static org.junit.Assert.*;

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
        testCompensation.setSalary(100000);
        LocalDate effectiveDate = LocalDate.parse("2023-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class).getBody();
        assertNotNull(createdCompensation);
        assertEquals(createdEmployee.getEmployeeId(), createdCompensation.getEmployeeId());
        assertEquals(100000, createdCompensation.getSalary());
        assertEquals(effectiveDate, createdCompensation.getEffectiveDate());
    }

    @Test
    public void testReadCompensation() {
        // Create compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary(100000);
        LocalDate effectiveDate = LocalDate.parse("2023-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);

        restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class);

        // Read compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertEquals(createdEmployee.getEmployeeId(), readCompensation.getEmployeeId());
        assertEquals(100000, readCompensation.getSalary());
        assertEquals(effectiveDate, readCompensation.getEffectiveDate());
    }

    @Test
    public void testCreateCompensationWithInvalidSalary() {
        // Create compensation with invalid salary
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary(-100); // Invalid salary, can't be negative
        LocalDate effectiveDate = LocalDate.parse("2023-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Compensation> entity = new HttpEntity<>(testCompensation, headers);

        ResponseEntity<String> response = restTemplate.exchange(compensationUrl + "/" + createdEmployee.getEmployeeId(), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Salary must be positive or zero"));
    }

    @Test
    public void testCreateCompensationWithInvalidDateFormat() {
        // Create compensation with invalid date format
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary(100000);
        String invalidDate = "01-01-2023"; // Invalid date format, should be yyyy-MM-dd

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String compensationJson = String.format("{\"employeeId\":\"%s\",\"salary\":100000,\"effectiveDate\":\"%s\"}", createdEmployee.getEmployeeId(), invalidDate);
        HttpEntity<String> entity = new HttpEntity<>(compensationJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(compensationUrl + "/" + createdEmployee.getEmployeeId(), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains("Invalid date format. Please use the format yyyy-MM-dd."));
    }

    @Test
    public void testCreateCompensationForSoftDeletedEmployee() {
        // Soft delete the employee
        restTemplate.delete(employeeUrl + "/" + createdEmployee.getEmployeeId());

        // Attempt to create compensation for the soft-deleted employee
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary(100000);
        LocalDate effectiveDate = LocalDate.parse("2023-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Compensation> entity = new HttpEntity<>(testCompensation, headers);

        ResponseEntity<String> response = restTemplate.exchange(compensationUrl + "/" + createdEmployee.getEmployeeId(), HttpMethod.POST, entity, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testReadCompensationForSoftDeletedEmployee() {
        // Create compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(createdEmployee.getEmployeeId());
        testCompensation.setSalary(100000);
        LocalDate effectiveDate = LocalDate.parse("2023-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);

        restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class);

        // Soft delete the employee
        restTemplate.delete(employeeUrl + "/" + createdEmployee.getEmployeeId());

        // Attempt to read compensation for the soft-deleted employee
        ResponseEntity<String> response = restTemplate.exchange(compensationIdUrl, HttpMethod.GET, null, String.class, createdEmployee.getEmployeeId());

        // Verify that the read attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }
}