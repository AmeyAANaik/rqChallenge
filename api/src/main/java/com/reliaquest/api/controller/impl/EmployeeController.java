package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.EmployeeRequest;
import com.reliaquest.api.service.EmplyeeService;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.NotNull;


@RestController
@RequestMapping("/api/employee")
@Validated
public class EmployeeController implements IEmployeeController<Employee, EmployeeRequest> {

    @Autowired
    private EmplyeeService emplyeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(emplyeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch( @PathVariable String searchString) {
        return ResponseEntity.ok(emplyeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable String id) {
        return ResponseEntity.ok(emplyeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(emplyeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(emplyeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeInput) {

        return ResponseEntity.ok(emplyeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById( @PathVariable String id) {
        return ResponseEntity.ok(emplyeeService.deleteById(id));
    }
}
