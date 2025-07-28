package com.reliaquest.api.service;

import com.reliaquest.api.exception.RemoteServiceException;
import com.reliaquest.api.exception.ResourceNotFoundException;
import com.reliaquest.api.model.DTO.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.DeleteRequest;
import com.reliaquest.api.model.request.EmployeeRequest;
import com.reliaquest.api.repository.EmployeeClient;
import com.reliaquest.api.transformer.RequestTransformer;
import com.reliaquest.api.utils.ApiResponses;
import java.util.List;
import java.util.Objects;

import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmplyeeService {

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private RequestTransformer requestTransformer;

    public List<Employee> getAllEmployees() {
        return ApiResponses.unwrap(employeeClient.findAll());
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return ApiResponses.unwrap(employeeClient.findAll()).stream()
                .filter(employee -> employee.getName().contains(searchString))
                .toList();
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the employee with the given ID
     * @throws IllegalArgumentException if the provided ID is null or blank
     * @throws ResourceNotFoundException if no employee is found with the given ID
     * @throws RemoteServiceException if there's an error communicating with the remote service
     */
    public Employee getEmployeeById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }


            ApiResponse<Employee> response = employeeClient.findById(id);

            // Check if the response indicates a not found scenario
            if (Objects.isNull(response) || response.getData() == null) {
                throw new ResourceNotFoundException("Employee", "ID", id);
            }

            try {
                return ApiResponses.unwrap(response);
            } catch (RemoteServiceException e) {
                // If the unwrap fails with null data, convert to ResourceNotFoundException
                if (e.getMessage().contains("Response data is null")) {
                    throw new ResourceNotFoundException("Employee", "ID", id);
                }
                throw e;
            }

    }

    public Integer getHighestSalaryOfEmployees() {
        return ApiResponses.unwrap(employeeClient.findAll()).stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        return ApiResponses.unwrap(employeeClient.findAll()).stream()
                .sorted((a, b) -> b.getSalary().compareTo(a.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .toList();
    }

    public Employee createEmployee(EmployeeRequest request) {
        return ApiResponses.unwrap(employeeClient.create(request));
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     * @return a string representation of the deleted employee
     * @throws IllegalArgumentException if the provided ID is null or blank
     * @throws ResourceNotFoundException if no employee is found with the given ID
     * @throws RemoteServiceException if there's an error communicating with the remote service
     */
    public String deleteById(String id) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

            // Find the employee first
            ApiResponse<Employee> response = employeeClient.findById(id);

            // Check if employee exists
            if (response.getData() == null) {
                throw new ResourceNotFoundException("Employee not found ", "ID", id);
            }

            // Delete the employee by name
            Employee employee = ApiResponses.unwrap(response);
            DeleteRequest request =
                    DeleteRequest.builder().name(employee.getName()).build();

            Boolean deleted = ApiResponses.unwrap(employeeClient.deleteByName(request));
            if (!deleted) {
                throw new RemoteServiceException("Failed to delete employee with ID: " + id);
            }

            return employee.getName().toString();


    }
}
