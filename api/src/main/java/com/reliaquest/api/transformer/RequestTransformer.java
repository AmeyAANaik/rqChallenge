package com.reliaquest.api.transformer;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.request.EmployeeRequest;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class RequestTransformer implements Function<EmployeeRequest, Employee> {

    @Override
    public Employee apply(EmployeeRequest employeeRequest) {
        return Employee.builder()
                .name(employeeRequest.getName())
                .salary(employeeRequest.getSalary())
                .age(employeeRequest.getAge())
                .title(employeeRequest.getTitle())
                .build();
    }
}
