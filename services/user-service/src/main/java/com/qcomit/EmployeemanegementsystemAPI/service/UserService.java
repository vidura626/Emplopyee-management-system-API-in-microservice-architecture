package com.qcomit.EmployeemanegementsystemAPI.service;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;

public interface UserService {

    boolean existsByUsername(String username);

    UserDto findByUsername(String name);

    EmployeeDto findEmployeeByUserId(Long userId, String token);

    UserDto saveUser(UserDto user);

    void updateUser(UserDto user);
}
