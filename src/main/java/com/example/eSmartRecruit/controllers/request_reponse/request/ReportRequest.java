package com.example.eSmartRecruit.controllers.request_reponse.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    @NotBlank(message = "This must be filled!")
    @Pattern(message = "Please filled right format!",regexp = "^[a-zA-Z0-9]+$")
    private String ReportName;
    @NotBlank(message = "This must be filled!")
    @Pattern(message = "Please filled right format!",regexp = "^[a-zA-Z0-9 ]+$")
    private String ReportData; 
}
