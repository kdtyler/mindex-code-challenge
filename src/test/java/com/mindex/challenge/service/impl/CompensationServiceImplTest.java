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

import static com.mindex.challenge.exceptionhandling.ErrorMessages.*;
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
        testEmployee.setFirstName("Kevin");
        testEmployee.setLastName("Tyler");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create employee in database. Assigns employeeId
        createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());
    }

    @Test
    public void testCreateCompensation() {
        // Create test compensation
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(createdEmployee.getEmployeeId(), 100000, effectiveDateAsString);

        // Post the compensation
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class).getBody();

        // Verify that the created compensation is not null and is equivalent to the test compensation
        assertNotNull(createdCompensation);
        assertCompensationEquivalence(testCompensation, createdCompensation);
    }

    @Test
    public void testReadCompensation() {
        // Create compensation
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(createdEmployee.getEmployeeId(), 100000, effectiveDateAsString);

        restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class);

        // Read compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();

        // Verify that the read compensation is not null and is equivalent to the test compensation
        assertNotNull(readCompensation);
        assertCompensationEquivalence(testCompensation, readCompensation);
    }

    @Test
    public void testCreateCompensationWithNegativeSalary() {
        // Create compensation
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(createdEmployee.getEmployeeId(), -100, effectiveDateAsString);

        // Test the post for the created compensation
        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Salary must be positive or zero"));
    }

    @Test
    public void testCreateCompensationWithNullSalary() {
        // Create compensation JSON with null salary
        String compensationJson = String.format("{\"employeeId\":\"%s\",\"salary\":null,\"effectiveDate\":\"2023-01-01\"}", createdEmployee.getEmployeeId());

        // Need to manually set due to null salary
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(compensationJson, headers);

        // Attempt to post the compensation
        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), entity, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Salary cannot be null"));
    }

    @Test
    public void testCreateCompensationWithInvalidDateFormat() {
        // Compensation JSON with invalid date format
        String invalidDate = "01-01-2023"; // Invalid date format, should be yyyy-MM-dd
        String compensationJson = String.format("{\"employeeId\":\"%s\",\"salary\":100000,\"effectiveDate\":\"%s\"}", createdEmployee.getEmployeeId(), invalidDate);

        // Need to manually set due to invalid date format
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(compensationJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), entity, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(INVALID_DATE_FORMAT));
    }

    @Test
    public void testCreateCompensationWithNullDate() {
        // Create compensation JSON with null effective date
        String compensationJson = String.format("{\"employeeId\":\"%s\",\"salary\":100000,\"effectiveDate\":null}", createdEmployee.getEmployeeId());

        // Need to manually set due to null effective date
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(compensationJson, headers);

        // Attempt to post the compensation
        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), entity, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Effective date cannot be null"));
    }

    @Test
    public void testReadCompensationWhenNoCompensationExists() {
        // Attempt to read compensation for an employee with no compensation
        ResponseEntity<String> response = restTemplate.getForEntity(compensationIdUrl, String.class, createdEmployee.getEmployeeId());

        // Verify that the read attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(COMPENSATION_NOT_FOUND));
    }

    @Test
    public void testCreateCompensationForNonExistentEmployee() {
        // Create compensation for a non-existent employee
        String nonExistentEmployeeId = "non-existent-id";
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(nonExistentEmployeeId, 100000, effectiveDateAsString);

        // Attempt to post the compensation
        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + nonExistentEmployeeId, testCompensation, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testReadCompensationForNonExistentEmployee() {
        // Attempt to read compensation for a non-existent employee
        String nonExistentEmployeeId = "non-existent-id";
        ResponseEntity<String> response = restTemplate.getForEntity(compensationIdUrl, String.class, nonExistentEmployeeId);

        // Verify that the read attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testCreateCompensationForSoftDeletedEmployee() {
        // Soft delete the employee
        restTemplate.delete(employeeUrl + "/" + createdEmployee.getEmployeeId());

        // Create compensation
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(createdEmployee.getEmployeeId(), 100000, effectiveDateAsString);

        // Attempt to post a compensation for a soft-deleted employee
        ResponseEntity<String> response = restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, String.class);

        // Verify that the creation attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testReadCompensationForSoftDeletedEmployee() {
        // Create compensation
        String effectiveDateAsString = "2023-01-01";
        Compensation testCompensation = createTestCompensation(createdEmployee.getEmployeeId(), 100000, effectiveDateAsString);

        // Post the compensation
        restTemplate.postForEntity(compensationUrl + "/" + createdEmployee.getEmployeeId(), testCompensation, Compensation.class);

        // Soft delete the employee
        restTemplate.delete(employeeUrl + "/" + createdEmployee.getEmployeeId());

        // Attempt to read compensation for the soft-deleted employee
        ResponseEntity<String> response = restTemplate.getForEntity(compensationIdUrl, String.class, createdEmployee.getEmployeeId());

        // Verify that the read attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    private Compensation createTestCompensation(String employeeId, int salary, String effectiveDateStr) {
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(employeeId);
        testCompensation.setSalary(salary);
        LocalDate effectiveDate = LocalDate.parse(effectiveDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        testCompensation.setEffectiveDate(effectiveDate);
        return testCompensation;
    }

    private void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}