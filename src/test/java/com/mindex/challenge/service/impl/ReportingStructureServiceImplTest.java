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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeUrl;
    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private Employee employee4;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        employeeUrl = "http://localhost:" + port + "/employee";

        /*
        Create employee objects for testing. Structure is as follows:
        employee1 -> employee2, employee3.
        employee3 -> employee4

        structure = (boss) -> (direct reports)
        */
        employee1 = new Employee();
        employee1.setFirstName("Kevin");
        employee1.setLastName("Layer1");
        employee1.setDepartment("Engineering");
        employee1.setPosition("Developer");

        employee2 = new Employee();
        employee2.setFirstName("David");
        employee2.setLastName("Layer2");
        employee2.setDepartment("Engineering");
        employee2.setPosition("Developer");

        employee3 = new Employee();
        employee3.setFirstName("Cindy");
        employee3.setLastName("Layer2");
        employee3.setDepartment("Engineering");
        employee3.setPosition("Developer");

        employee4 = new Employee();
        employee4.setFirstName("Larry");
        employee4.setLastName("Layer3");
        employee4.setDepartment("Engineering");
        employee4.setPosition("Developer");

        employee1.setDirectReports(Arrays.asList(employee2, employee3));
        employee3.setDirectReports(Arrays.asList(employee4));

        employee1 = restTemplate.postForEntity(employeeUrl, employee1, Employee.class).getBody();
        employee2 = restTemplate.postForEntity(employeeUrl, employee2, Employee.class).getBody();
        employee3 = restTemplate.postForEntity(employeeUrl, employee3, Employee.class).getBody();
        employee4 = restTemplate.postForEntity(employeeUrl, employee4, Employee.class).getBody();

        // Update employee objects to include direct reports (necessary due to how assignment of employeeId works)
        employee3.setDirectReports(Arrays.asList(employee4));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee3, headers);
        restTemplate.exchange(employeeUrl + "/{id}", HttpMethod.PUT, request, Employee.class, employee3.getEmployeeId());

        employee1.setDirectReports(Arrays.asList(employee2, employee3));
        request = new HttpEntity<>(employee1, headers);
        restTemplate.exchange(employeeUrl + "/{id}", HttpMethod.PUT, request, Employee.class, employee1.getEmployeeId());
    }

    // Tests reporting structure for employee1 (Kevin Layer1). This should output 3. 2 are directly below Kevin, and 1 is directly below a subordinate of Kevin
    @Test
    public void testGetReportingStructureByEmployeeId() {
        Employee testEmployee = restTemplate.getForEntity(employeeUrl + "/{id}", Employee.class, employee1.getEmployeeId()).getBody();
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());
        assertEquals(testEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(3, reportingStructure.getNumberOfReports());
    }
}