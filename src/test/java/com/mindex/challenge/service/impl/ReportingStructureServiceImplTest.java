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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.UUID;

import static com.mindex.challenge.exceptionhandling.ErrorMessages.EMPLOYEE_NOT_FOUND;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeUrl;
    private Employee bigBoss;
    private Employee middleMan1;
    private Employee middleMan2;
    private Employee gruntUnderMiddleMan2;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        employeeUrl = "http://localhost:" + port + "/employee";

        /*
        * Create employee objects for testing. Structure is as follows:
        * bigBoss -> middleMan1, middleMan2.
        * middleMan2 -> gruntUnderMiddleMan2
        *
        * structure: (boss) -> (direct reports)
        */
        bigBoss = createEmployee("Kevin", "Layer1", "Engineering", "Developer");
        middleMan1 = createEmployee("David", "Layer2", "Engineering", "Developer");
        middleMan2 = createEmployee("Cindy", "Layer2", "Engineering", "Developer");
        gruntUnderMiddleMan2 = createEmployee("Larry", "Layer3", "Engineering", "Developer");

        bigBoss.setDirectReports(Arrays.asList(middleMan1, middleMan2));
        middleMan2.setDirectReports(Arrays.asList(gruntUnderMiddleMan2));

        bigBoss = restTemplate.postForEntity(employeeUrl, bigBoss, Employee.class).getBody();
        middleMan1 = restTemplate.postForEntity(employeeUrl, middleMan1, Employee.class).getBody();
        middleMan2 = restTemplate.postForEntity(employeeUrl, middleMan2, Employee.class).getBody();
        gruntUnderMiddleMan2 = restTemplate.postForEntity(employeeUrl, gruntUnderMiddleMan2, Employee.class).getBody();

        // Update employee objects to include direct reports (necessary due to how assignment of employeeId works)
        middleMan2.setDirectReports(Arrays.asList(gruntUnderMiddleMan2));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(middleMan2, headers);
        restTemplate.exchange(employeeUrl + "/{id}", HttpMethod.PUT, request, Employee.class, middleMan2.getEmployeeId());

        bigBoss.setDirectReports(Arrays.asList(middleMan1, middleMan2));
        request = new HttpEntity<>(bigBoss, headers);
        restTemplate.exchange(employeeUrl + "/{id}", HttpMethod.PUT, request, Employee.class, bigBoss.getEmployeeId());
    }

    // Tests reporting structure for bigBoss. This should have numberOfReports = 3. Two middle men are below bigBoss, and a grunt is below middleMan2
    @Test
    public void testGetReportingStructureByEmployeeId() {
        Employee testEmployee = restTemplate.getForEntity(employeeUrl + "/{id}", Employee.class, bigBoss.getEmployeeId()).getBody();
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(testEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(3, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureForEmployeeWithNoDirectReports() {
        Employee employeeWithNoReports = restTemplate.getForEntity(employeeUrl + "/{id}", Employee.class, gruntUnderMiddleMan2.getEmployeeId()).getBody();
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeWithNoReports.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(employeeWithNoReports.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReportingStructureExcludesSoftDeletedEmployeesAtLeaves() {
        // Soft delete gruntUnderMiddleMan2
        restTemplate.delete(employeeUrl + "/" + gruntUnderMiddleMan2.getEmployeeId());

        // Get reporting structure for bigBoss
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, bigBoss.getEmployeeId()).getBody();

        // Verify that the reporting structure does not include soft-deleted gruntUnderMiddleMan2
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(bigBoss.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(2, reportingStructure.getNumberOfReports()); // gruntUnderMiddleMan2 no longer counted
    }

    @Test
    public void testReportingStructureWithSoftDeletedEmployeeInHierarchy() {
        // Soft delete middleMan2
        restTemplate.delete(employeeUrl + "/" + middleMan2.getEmployeeId());

        // Get reporting structure for bigBoss
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, bigBoss.getEmployeeId()).getBody();

        // Verify that the reporting structure calculation is correct
        // middleMan2 not counted because it's soft-deleted. gruntUnderMiddleMan2 also not counted because chain of reports broken to it from root
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(bigBoss.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(1, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureForNonExistentEmployee() {
        Employee nonExistentEmployee = new Employee();
        nonExistentEmployee.setEmployeeId(UUID.randomUUID().toString());
        nonExistentEmployee.setFirstName("Nonexistent");
        nonExistentEmployee.setLastName("Employee");
        nonExistentEmployee.setDepartment("Engineering");
        nonExistentEmployee.setPosition("Developer");

        ResponseEntity<String> response = restTemplate.getForEntity(reportingStructureUrl, String.class, nonExistentEmployee.getEmployeeId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(EMPLOYEE_NOT_FOUND));
    }

    private Employee createEmployee(String firstName, String lastName, String department, String position) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDepartment(department);
        employee.setPosition(position);

        return employee;
    }
}