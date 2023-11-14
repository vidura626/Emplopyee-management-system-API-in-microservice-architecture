package com.qcomit.EmployeemanegementsystemAPI.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Name {
    @Length(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @Length(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
