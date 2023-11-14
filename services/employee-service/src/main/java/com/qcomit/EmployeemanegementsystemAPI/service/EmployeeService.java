package com.qcomit.EmployeemanegementsystemAPI.service;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import io.jsonwebtoken.Claims;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    public Long saveEmployee(EmployeeDto employee) throws IOException;

    public EmployeeDto updateEmployee(EmployeeDto employee, Long id, String token);

    public void deleteEmployee(Long id, String token);

    public String uploadProfileImage(MultipartFile image, Long employeeId, String token) throws IOException;

    public byte[] downloadProfileImage(Long employeeId) throws IOException;


    public List<EmployeeDto> findAllEmployees(String token);

    public EmployeeDto findEmployeeById(Long id, String token);

    public void refreshEmployeeAgeWithDays();

    EmployeeDto findEmployeeByUserId(Long id);
}
