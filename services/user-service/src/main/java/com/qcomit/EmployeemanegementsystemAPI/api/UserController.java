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
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<EmployeeDto> login(Authentication authentication,
                                             @RequestAttribute("token") String token) {
        if (authentication != null) {
            UserDto userDto = userService.findByUsername(authentication.getName());
            return ResponseEntity.ok().body(userService.findEmployeeByUserId(userDto.getUser_id(), token));
        }
        throw new NotFoundException("User not found.");
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> findUserDetailsByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok().body(userService.findByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserDetailsById(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserDetailsById(@PathVariable("id") String id) {
        userService.deleteById(id);
        return ResponseEntity.ok().body("Deleted");
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto user) throws IOException {
        return ResponseEntity.ok().body(userService.saveUser(user));
    }

    @PutMapping
    public ResponseEntity<Boolean> updateUser(@RequestBody @Valid UserDto user) throws IOException {
        userService.updateUser(user);
        return ResponseEntity.ok().body(true);
    }
}
