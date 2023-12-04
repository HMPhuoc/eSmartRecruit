package com.example.eSmartRecruit.authentication.request_reponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NonNull
    @NotBlank(message = "Must be filled")
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String username;
    @NotBlank
    @NotNull
    private String password;
}
