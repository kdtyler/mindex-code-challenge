package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

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
//        testEmployee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
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

        System.out.println("createdEmployee firstName: " + createdEmployee.getFirstName());
        System.out.println("createdEmployee employeeID: " + createdEmployee.getEmployeeId());


        assertNotNull(createdEmployee.getEmployeeId());
        System.out.println("createdEmployee firstName: " + createdEmployee.getFirstName());
        System.out.println("createdEmployee employeeID: " + createdEmployee.getEmployeeId());
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
    public void testCreateEmployeeWithNullFirstName() {
        testEmployee.setFirstName(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullLastName() {
        testEmployee.setLastName(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullDepartment() {
        testEmployee.setDepartment(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithNullPosition() {
        testEmployee.setPosition(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyFirstName() {
        testEmployee.setFirstName("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyLastName() {
        testEmployee.setLastName("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyDepartment() {
        testEmployee.setDepartment("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testCreateEmployeeWithEmptyPosition() {
        testEmployee.setPosition("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    // This test will be unneeded if JSONIgnore added to employeeID
    @Test
    public void testCreateEmployeeWithInvalidEmployeeId() {
        testEmployee.setEmployeeId("invalid-id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(employeeUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Employee ID must be a valid UUID"));
    }

    @Test
    public void testUpdateEmployeeWithNullFirstName() {
        testEmployee.setFirstName(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyFirstName() {
        testEmployee.setFirstName("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullLastName() {
        testEmployee.setLastName(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyLastName() {
        testEmployee.setLastName("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Last name cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullDepartment() {
        testEmployee.setDepartment(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyDepartment() {
        testEmployee.setDepartment("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Department cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithNullPosition() {
        testEmployee.setPosition(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

        ResponseEntity<String> response = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, request, String.class, testEmployee.getEmployeeId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Position cannot be null or empty"));
    }

    @Test
    public void testUpdateEmployeeWithEmptyPosition() {
        testEmployee.setPosition("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(testEmployee, headers);

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
}
