package com.example.eSmartRecruit.controllers.request_reponse.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionRequest {
    @NotBlank(message = "Title must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Title should contain only letters and numbers")
    private String title;

    @NotBlank(message = "Job description must not be blank")
    @Pattern(regexp = "^[\\w\\s.,!?:;'\"-]+$", message = "Invalid job description format")
    private String jobDescription;

    @NotBlank(message = "Job requirements must not be blank")
    @Pattern(regexp = "^[\\w\\s.,!?:;'\"-]+$", message = "Invalid job requirements format")
    private String jobRequirements;

    @DecimalMin(value = "0", message = "Salary must be greater than or equal to 0")
    private BigDecimal salary;

    @Future(message = "Expiration date should be in the future")
    private Date expireDate;

    @NotBlank(message = "Location must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Location should contain only letters and numbers")
    private String location;

}
