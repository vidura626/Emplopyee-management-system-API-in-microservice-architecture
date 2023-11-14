package com.qcomit.EmployeemanegementsystemAPI.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Address;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Contact;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Name;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employee_id;
    @Embedded
    private Name name;
    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Birthday cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;
    private String profileImage;
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Long userId;
    private int current_age_in_days;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;
    @Embedded
    @NotNull(message = "Address cannot be null")
    private Address address;
    @NotNull(message = "Contact cannot be null")
    @ElementCollection
    @CollectionTable(
            name = "contacts",
            joinColumns = @JoinColumn(name = "EmployeeId")
    )
    List<Contact> contacts;
    @Positive(message = "Salary cannot be negative")
    private double salary;
}
