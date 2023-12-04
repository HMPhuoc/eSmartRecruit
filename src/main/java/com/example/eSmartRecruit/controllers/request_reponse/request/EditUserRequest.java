package com.example.eSmartRecruit.controllers.request_reponse.request;

import com.example.eSmartRecruit.models.enumModel.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditUserRequest {
    @NotBlank(message = "Must be filled")
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    String username;
    @NotBlank
    @NotNull
    String password;
    @NotBlank(message = "This must be filled!")
    @Pattern(message = "Please filled right format!",regexp = "^(.+)@(.+)$")
    String email;
    @NotBlank(message = "This must be filled!")
    @Pattern(message = "Please filled right format!",regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    String phonenumber;
    @NotBlank(message = "Role must be declared!")
    String rolename;
    @NotBlank(message = "Status must be declared!")
    String status;
}
