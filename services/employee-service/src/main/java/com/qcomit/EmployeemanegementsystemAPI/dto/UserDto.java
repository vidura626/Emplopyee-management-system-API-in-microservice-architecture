package com.qcomit.EmployeemanegementsystemAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private Long user_id;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    private String password;
    @NotEmpty(message = "Role cannot be empty")
    private String role;
}
