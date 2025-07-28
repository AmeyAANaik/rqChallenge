package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.reliaquest.api.exception.RemoteServiceException;
import com.reliaquest.api.exception.ResourceNotFoundException;
import com.reliaquest.api.model.DTO.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.DeleteRequest;
import com.reliaquest.api.model.request.EmployeeRequest;
import com.reliaquest.api.repository.EmployeeClient;
import com.reliaquest.api.transformer.RequestTransformer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private RequestTransformer requestTransformer;

    @InjectMocks
    private EmplyeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        employee1 = Employee.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .salary(100000)
                .age(30)
                .title("Developer")
                .email("john@example.com")
                .build();

        employee2 = Employee.builder()
                .id(UUID.randomUUID())
                .name("Jane Smith")
                .salary(120000)
                .age(35)
                .title("Senior Developer")
                .email("jane@example.com")
                .build();

        employeeRequest = new EmployeeRequest();
        // Set employeeRequest fields as needed
    }

    @Test
    void getAllEmployeesShouldReturnAllEmployees() {

        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeClient.findAll()).thenReturn(new ApiResponse<>("Successfully processed request.", employees));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        verify(employeeClient).findAll();
    }

    @Test
    void getEmployeesByNameSearchShouldReturnMatchingEmployees() {
        List<Employee> employees = List.of(employee1);
        when(employeeClient.findAll())
                .thenReturn(new ApiResponse<>("Successfully processed request.", List.of(employee1, employee2)));

        List<Employee> result = employeeService.getEmployeesByNameSearch("John");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void getEmployeeByIdShouldReturnEmployee_WhenExists() {

        String employeeId = employee1.getId().toString();
        when(employeeClient.findById(employeeId))
                .thenReturn(new ApiResponse<>("Successfully processed request.", employee1));

        Employee result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.getId().toString());
        verify(employeeClient).findById(employeeId);
    }

    @Test
    void getEmployeeByIdShouldThrowResourceNotFoundExceptionWhenEmployeeNotFound() {

        String nonExistentId = "non-existent-id";

        when(employeeClient.findById(nonExistentId))
                .thenReturn(new ApiResponse<>("Successfully processed request.", null));

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(nonExistentId));

        verify(employeeClient).findById(nonExistentId);
    }

    @Test
    void getEmployeeByIdShouldHandleRemoteServiceError() {

        String nonExistentId = "non-existent-id";

        when(employeeClient.findById(nonExistentId)).thenReturn(new ApiResponse<>("Employee not found", null));

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(nonExistentId));

        verify(employeeClient).findById(nonExistentId);
    }

    @Test
    void getEmployeeByIdShouldThrowIllegalArgumentException_WhenIdIsNull() {

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(null));

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(employeeClient);
    }

    @Test
    void getEmployeeByIdShouldThrowIllegalArgumentException_WhenIdIsBlank() {

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById("   "));

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(employeeClient);
    }

    @Test
    void getEmployeeByIdShouldPropagateRemoteServiceExceptionWhenRemoteServiceFails() {

        String employeeId = "123";
        when(employeeClient.findById(employeeId)).thenThrow(new RemoteServiceException("Remote service unavailable"));

        RemoteServiceException exception =
                assertThrows(RemoteServiceException.class, () -> employeeService.getEmployeeById(employeeId));

        assertEquals("Error retrieving employee: Remote service unavailable", exception.getMessage());
        verify(employeeClient).findById(employeeId);
    }

    @Test
    void getHighestSalaryOfEmployeesShouldReturnHighestSalary() {

        when(employeeClient.findAll())
                .thenReturn(new ApiResponse<>("Successfully processed request.", List.of(employee1, employee2)));

        int highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(120000, highestSalary);
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesShouldReturnTopEarners() {

        when(employeeClient.findAll())
                .thenReturn(new ApiResponse<>("Successfully processed request.", List.of(employee1, employee2)));

        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(2, topEarners.size());
        assertEquals("Jane Smith", topEarners.get(0)); // Highest salary first
    }

    @Test
    void createEmployeeShouldReturnCreatedEmployee() {

        when(employeeClient.create(any(EmployeeRequest.class)))
                .thenReturn(new ApiResponse<>("Successfully processed request.", employee1));

        Employee result = employeeService.createEmployee(employeeRequest);

        assertNotNull(result);
        assertEquals(employee1.getId(), result.getId());
        verify(employeeClient).create(employeeRequest);
    }

    @Test
    void deleteByIdShouldReturnEmployeeString_WhenDeleted() {

        String employeeId = employee1.getId().toString();
        when(employeeClient.findById(employeeId))
                .thenReturn(new ApiResponse<>("Successfully processed request.", employee1));
        when(employeeClient.deleteByName(any())).thenReturn(new ApiResponse<>("Successfully processed request.", true));

        String result = employeeService.deleteById(employeeId);

        assertNotNull(result);
        assertTrue(result.contains(employee1.getName()));
        verify(employeeClient).findById(employeeId);
        verify(employeeClient).deleteByName(any(DeleteRequest.class));
    }

    @Test
    void deleteByIdShouldThrowResourceNotFoundException_WhenEmployeeNotFound() {

        String nonExistentId = "non-existent-id";
        when(employeeClient.findById(nonExistentId))
                .thenReturn(new ApiResponse<>("Successfully processed request.", null));

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteById(nonExistentId));

        verify(employeeClient).findById(nonExistentId);
        verify(employeeClient, never()).deleteByName(any());
    }

    @Test
    void deleteByIdShouldThrowIllegalArgumentException_WhenIdIsNull() {

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> employeeService.deleteById(null));

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(employeeClient);
    }

    @Test
    void deleteByIdShouldThrowIllegalArgumentException_WhenIdIsBlank() {

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> employeeService.deleteById("   "));

        assertEquals("Employee ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(employeeClient);
    }

    @Test
    void deleteByIdShouldThrowRemoteServiceExceptionWhenDeleteFails() {

        String employeeId = employee1.getId().toString();
        when(employeeClient.findById(employeeId))
                .thenReturn(new ApiResponse<>("Successfully processed request.", employee1));
        when(employeeClient.deleteByName(any()))
                .thenReturn(new ApiResponse<>("Successfully processed request.", false));

        RemoteServiceException exception =
                assertThrows(RemoteServiceException.class, () -> employeeService.deleteById(employeeId));

        assertEquals("Failed to delete employee with ID: " + employeeId, exception.getMessage());
        verify(employeeClient).findById(employeeId);
        verify(employeeClient).deleteByName(any(DeleteRequest.class));
    }
}
