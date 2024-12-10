package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
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

import java.util.UUID;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.EMPLOYEE_NOT_FOUND;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private Employee testEmployee;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {

        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";

        testEmployee = new Employee();
        testEmployee.setEmployeeId(UUID.randomUUID().toString());
        testEmployee.setFirstName("Kevin");
        testEmployee.setLastName("Tyler");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
    }

    @Test
    public void testCreateReadUpdate() {

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testReadSoftDeletedEmployee() {
        // Create an employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        // soft delete
        restTemplate.delete(employeeIdUrl, createdEmployee.getEmployeeId());

        // Attempt to read the soft-deleted employee
        ResponseEntity<String> response = restTemplate.getForEntity(employeeIdUrl, String.class, createdEmployee.getEmployeeId());

        // Verify that the employee is not found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testUpdateSoftDeletedEmployee() {
        // Create an employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        // Soft delete the employee
        restTemplate.delete(employeeIdUrl, createdEmployee.getEmployeeId());

        // Attempt to update the soft-deleted employee
        createdEmployee.setPosition("Updated Position");

        HttpEntity<Employee> request = createHttpEntity(createdEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, createdEmployee.getEmployeeId());

        // Verify that the update attempt returns the correct error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testReadNonExistentEmployee() {
        String nonExistentEmployeeId = UUID.randomUUID().toString();

        ResponseEntity<String> response = restTemplate.getForEntity(employeeIdUrl, String.class, nonExistentEmployeeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testUpdateNonExistentEmployee() {
        testEmployee.setEmployeeId(UUID.randomUUID().toString());

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    @Test
    public void testCreateEmployeeWithNullFirstName() {
        testEmployee.setFirstName(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullLastName() {
        testEmployee.setLastName(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullDepartment() {
        testEmployee.setDepartment(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullPosition() {
        testEmployee.setPosition(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyFirstName() {
        testEmployee.setFirstName("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyLastName() {
        testEmployee.setLastName("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyDepartment() {
        testEmployee.setDepartment("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyPosition() {
        testEmployee.setPosition("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    // This test will be unneeded if JSONIgnore added to employeeID
    @Test
    public void testCreateEmployeeWithInvalidEmployeeId() {
        testEmployee.setEmployeeId("invalid-id");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Employee ID must be a valid UUID"));
    }

    @Test
    public void testUpdateEmployeeWithNullFirstName() {
        testEmployee.setFirstName(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyFirstName() {
        testEmployee.setFirstName("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullLastName() {
        testEmployee.setLastName(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyLastName() {
        testEmployee.setLastName("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullDepartment() {
        testEmployee.setDepartment(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyDepartment() {
        testEmployee.setDepartment("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullPosition() {
        testEmployee.setPosition(null);

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyPosition() {
        testEmployee.setPosition("");

        HttpEntity<Employee> request = createHttpEntity(testEmployee);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private HttpEntity<Employee> createHttpEntity(Employee employee) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(employee, headers);
    }
}
