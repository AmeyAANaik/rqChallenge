package com.reliaquest.api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteRequest {

    String name;
}
