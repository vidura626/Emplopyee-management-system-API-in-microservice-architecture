package com.qcomit.EmployeemanegementsystemAPI.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qcomit.EmployeemanegementsystemAPI.dto.embedded.AddressDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.embedded.ContactDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDto {
    private Long employee_id;
    @Length(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;
    @Length(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;
    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Birthday cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;
    @Pattern(message = "Role is not valid", regexp = "ADMIN|USER")
    private String role;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int current_age_in_days;
    @NotNull(message = "Address cannot be null")
    private AddressDto address;
    @NotNull(message = "Contact cannot be null")
    List<ContactDto> contacts;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "Asia/Colombo")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "Asia/Colombo")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modified;
    @Min(value = 20000, message = "Salary cannot be less than 20000")
    private double salary;
}
