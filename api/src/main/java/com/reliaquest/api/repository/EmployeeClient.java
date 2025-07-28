package com.reliaquest.api.repository;

import com.reliaquest.api.config.FeignClientConfig;
import com.reliaquest.api.model.DTO.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.DeleteRequest;
import com.reliaquest.api.model.request.EmployeeRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "employeeClient",
        url = "${employee.service.base-url:http://localhost:8112/api/v1/employee}",
        configuration = FeignClientConfig.class,
        fallback = EmployeeClientFallback.class)
public interface EmployeeClient {

    @GetMapping
    ApiResponse<List<Employee>> findAll();

    @PostMapping
    ApiResponse<Employee> create(@RequestBody EmployeeRequest employee);

    @DeleteMapping
    ApiResponse<Boolean> deleteByName(@RequestBody DeleteRequest request);

    @GetMapping("/{id}")
    ApiResponse<Employee> findById(@PathVariable String id);
}
