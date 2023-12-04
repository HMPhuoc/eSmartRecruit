package com.example.eSmartRecruit.authentication.request_reponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username must be filled")
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String username; // chữ thường và số

    @NotBlank(message = "This must be filled!")
    @Pattern(regexp = "^(.+)@(.+)$")
    private String email;

    @NotBlank(message = "Must be filled")
    @Pattern(regexp = "((?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!]).{6,20})")
    private String password; // chứa các kí tự hoa, thường, đặc biệt, số, độ dài từ 6-20

    @NotBlank(message = "This must be filled!")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String phoneNumber; // Số đt có dạng mở đầu bằng 84 hoặc 0, số tiếp theo là 3/5/7/8/9 và sau đó là 8 số bất kì

    @NotBlank(message = "Role must be declared!")
    private String roleName;

}
