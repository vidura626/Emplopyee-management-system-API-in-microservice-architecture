package com.qcomit.EmployeemanegementsystemAPI.api;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.service.UserService;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<EmployeeDto> login(Authentication authentication, @RequestHeader("Authorization") String token) {
        if(authentication!=null){
            UserDto userDto = userService.findByUsername(authentication.getName());
            return ResponseEntity.ok().body(userService.findEmployeeByUserId(userDto.getUser_id(),token));
        } throw new NotFoundException("User not found.");
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto user) throws IOException {
        return ResponseEntity.ok().body(userService.saveUser(user));
    }
}
