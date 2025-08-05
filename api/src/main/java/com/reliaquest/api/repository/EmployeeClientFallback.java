package com.reliaquest.api.repository;

import com.reliaquest.api.model.DTO.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.DeleteRequest;
import com.reliaquest.api.model.request.EmployeeRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeClientFallback implements EmployeeClient {

    @Override
    public ApiResponse<List<Employee>> findAll() {
        log.error("Fallback: Could not fetch all employees. Service unavailable.");
        return new ApiResponse<>("Service temporarily unavailable. Please try again later.", null);
    }

    @Override
    public ApiResponse<Employee> create(EmployeeRequest employee) {
        log.error("Fallback: Could not create employee. Service unavailable.");
        return new ApiResponse<>("Failed to create employee. Service unavailable.", null);
    }

    @Override
    public ApiResponse<Boolean> deleteByName(DeleteRequest request) {
        log.error("Fallback: Could not delete employee. Service unavailable.");
        return new ApiResponse<>("Failed to delete employee. Service unavailable.", null);
    }

    @Override
    public ApiResponse<Employee> findById(String id) {
        log.error("Fallback: Could not find employee with id {}. Service unavailable.", id);
        return new ApiResponse<>("Failed to fetch employee details. Service unavailable.", null);
    }
}
