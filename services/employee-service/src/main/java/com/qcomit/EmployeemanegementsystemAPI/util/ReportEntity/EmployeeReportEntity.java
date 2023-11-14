package com.qcomit.EmployeemanegementsystemAPI.util.ReportEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeReportEntity {
    private String employeeId;
    private String profileImage;
    private String name;
    private String email;
    private String address;
    private String contacts;
    private String birthday;
    private double salary;
    private String created;
    private String modified;

}
