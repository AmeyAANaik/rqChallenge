package com.reliaquest.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.request.EmployeeRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequest validEmployeeRequest;

    private final Random random = new Random();
    private static final List<String> FIRST_NAMES = Arrays.asList(
            "John",
            "Jane",
            "Michael",
            "Emily",
            "David",
            "Sarah",
            "Robert",
            "Jennifer",
            "William",
            "Elizabeth",
            "James",
            "Linda",
            "Richard",
            "Patricia");
    private static final List<String> LAST_NAMES = Arrays.asList(
            "Smith",
            "Johnson",
            "Williams",
            "Brown",
            "Jones",
            "Miller",
            "Davis",
            "Garcia",
            "Rodriguez",
            "Wilson",
            "Martinez",
            "Anderson");
    private static final List<String> TITLES = Arrays.asList(
            "Software Engineer",
            "Senior Developer",
            "Team Lead",
            "Architect",
            "QA Engineer",
            "DevOps Engineer",
            "Product Manager");
    private static final List<String> DOMAINS = Arrays.asList("example.com", "test.org", "demo.net", "company.io");

    private EmployeeRequest createRandomEmployeeRequest() {
        String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        String domain = DOMAINS.get(random.nextInt(DOMAINS.size()));

        EmployeeRequest request = new EmployeeRequest();
        request.setName(firstName + " " + lastName);
        request.setSalary(40000 + random.nextInt(120000)); // Salary between 40k and 160k
        request.setAge(22 + random.nextInt(43)); // Age between 22 and 65
        request.setTitle(TITLES.get(random.nextInt(TITLES.size())));

        return request;
    }

    @BeforeEach
    void setUp() {
        validEmployeeRequest = createRandomEmployeeRequest();
    }

    @Test
    void createEmployeeWithValidRequestShouldReturnCreatedEmployee() throws Exception {

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value(validEmployeeRequest.getName()));
    }

    @Test
    void createEmployeeWithInvalidRequestShouldReturnBadRequest() throws Exception {

        EmployeeRequest invalidRequest = new EmployeeRequest();

        invalidRequest.setName("");
        invalidRequest.setSalary(-1);
        invalidRequest.setAge(-1);
        invalidRequest.setTitle("");

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEmployeesShouldReturnEmployeeList() throws Exception {

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employee_name").isString())
                .andExpect(jsonPath("$[0].employee_salary").isNumber())
                .andExpect(jsonPath("$[0].employee_age").isNumber())
                .andExpect(jsonPath("$[0].employee_title").isString())
                .andExpect(jsonPath("$[0].employee_email").isString());
    }

    @Test
    void searchEmployeesWithMatchingNameShouldReturnResults() throws Exception {

        mockMvc.perform(get("/api/employee/search/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employee_name", containsString("John")));
    }

    @Test
    void deleteEmployeeWhenNotExistsShouldReturnNotFound() throws Exception {

        String nonExistentId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/employee/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(containsString("Employee not found with the given I")));
    }

    @Test
    void getHighestSalaryShouldReturnNumber() throws Exception {

        mockMvc.perform(get("/api/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void getTopTenHighestEarningEmployeesShouldReturnList() throws Exception {

        mockMvc.perform(get("/api/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(lessThanOrEqualTo(10))));
    }
}
