package com.example.eSmartRecruit.controllers.request_reponse.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateRequest {
    @NotBlank(message = "This must be filled!")
    String result;
}
