package com.qcomit.EmployeemanegementsystemAPI.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Address {
    @NotEmpty(message = "Address - Home No cannot be empty")
    private String homeNo;
    @NotEmpty(message = "Address - Street cannot be empty")
    private String street;
    @NotEmpty(message = "Address - City cannot be empty")
    private String city;
    @NotEmpty(message = "Address - Postal Code cannot be empty")
    private String postalCode;

    @Override
    public String toString() {
        return homeNo + " " + street + " " + city + " " + postalCode;
    }
}
