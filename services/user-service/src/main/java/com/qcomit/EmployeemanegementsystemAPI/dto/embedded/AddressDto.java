package com.qcomit.EmployeemanegementsystemAPI.dto.embedded;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressDto {
    @NotEmpty(message = "Address - Home No cannot be empty")
    private String homeNo;
    @NotEmpty(message = "Address - Street cannot be empty")
    private String street;
    @NotEmpty(message = "Address - City cannot be empty")
    private String city;
    @NotEmpty(message = "Address - Postal Code cannot be empty")
    private String postalCode;
}
