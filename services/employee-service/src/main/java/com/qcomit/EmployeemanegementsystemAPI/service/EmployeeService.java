package com.qcomit.EmployeemanegementsystemAPI.service;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    public Long saveEmployee(EmployeeDto employee) throws IOException;

    public EmployeeDto updateEmployee(EmployeeDto employee, Long id);

    public void deleteEmployee(Long id);

    public String uploadProfileImage(MultipartFile image, Long employeeId) throws IOException;

    public byte[] downloadProfileImage(Long employeeId) throws IOException;


    public List<EmployeeDto> findAllEmployees();

    public EmployeeDto findEmployeeById(Long id);

    public void refreshEmployeeAgeWithDays();

    EmployeeDto findEmployeeByUserDto(UserDto user);
}
