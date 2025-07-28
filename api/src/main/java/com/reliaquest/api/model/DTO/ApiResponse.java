package com.reliaquest.api.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private String status;

    private T data;
}
