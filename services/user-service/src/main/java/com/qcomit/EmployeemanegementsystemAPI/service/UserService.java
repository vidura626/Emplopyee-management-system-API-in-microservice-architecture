package com.qcomit.EmployeemanegementsystemAPI.service;

import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;

public interface UserService {

    boolean existsByUsername(String username);

    UserDto findByUsername(String name);
}
