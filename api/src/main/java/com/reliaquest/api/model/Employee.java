package com.reliaquest.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Builder
@JsonNaming(Employee.PrefixNamingStrategy.class)
public class Employee {

    private UUID id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;

    static class PrefixNamingStrategy extends PropertyNamingStrategies.NamingBase {
        private static final String PREFIX = "employee_";

        @Override
        public String translate(String prop) {
            if ("id".equals(prop)) return prop;
            return PREFIX + prop;
        }
    }
}
