package com.qcomit.EmployeemanegementsystemAPI.api;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.service.UserService;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
//    private final EmployeeService employeeService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public EmployeeDto login(Authentication authentication) {
        if(authentication!=null){
            UserDto userDto = userService.findByUsername(authentication.getName());
//            todo  using webflux
//            return employeeService.findEmployeeByUserDto(userDto);
            return null;
        } throw new NotFoundException("User not found.");
    }
}
