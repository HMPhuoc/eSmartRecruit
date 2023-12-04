package com.example.eSmartRecruit.controllers.request_reponse.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewSessionRequest {
    @NotNull(message = "This must be filled!")
    Integer interviewerId;
    @NotNull(message = "This must be filled!")
    @Future(message = "Please fill a date in the future!")
    Date date;
    @NotBlank(message = "This must be filled!")
    @Pattern(message = "Please fill in the right format!", regexp = "^[a-zA-Z0-9!@#$%^&*()-=_+{}|;:'\",.<>?/\\[\\]\\\\]+$")
    String location;
    @NotBlank(message = "This must be filled!")
    String notes;


    String result;
}