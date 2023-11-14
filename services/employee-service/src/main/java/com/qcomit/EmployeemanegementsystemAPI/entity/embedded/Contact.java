package com.qcomit.EmployeemanegementsystemAPI.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Contact {
    @NotEmpty(message = "Contact - Description cannot be empty")
    private String description;
    @NotEmpty(message = "Contact - Phone cannot be empty")
    @Pattern(message = "Contact - Phone is not valid", regexp = "^[0-9]{10}$")
    private String phone;

    @Override
    public String toString() {
        return description + " : " + phone;
    }
}
