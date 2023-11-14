package com.qcomit.EmployeemanegementsystemAPI.service;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import jakarta.validation.Valid;

public interface UserService {

    boolean existsByUsername(String username);

    UserDto findByUsername(String name);

    EmployeeDto findEmployeeByUserId(Long userId, String token);

    UserDto saveUser(UserDto user);
}
