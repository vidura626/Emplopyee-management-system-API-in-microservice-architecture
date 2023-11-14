package com.qcomit.EmployeemanegementsystemAPI.service.impl;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.entity.User;
import com.qcomit.EmployeemanegementsystemAPI.repository.UserRepository;
import com.qcomit.EmployeemanegementsystemAPI.service.UserService;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.NotFoundException;
import com.qcomit.EmployeemanegementsystemAPI.util.mappers.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Mapper mapper, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username " + username);
        }
        return mapper.toDto(user);
    }

    @Override
    public EmployeeDto findEmployeeByUserId(Long userId, String token) {
        WebClient webClient = webClientBuilder.build();
        EmployeeDto employeeDto = webClient.get()
                .uri("http://localhost:8081/api/v1/employee/userId/" + userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(EmployeeDto.class)
                .doOnError(throwable -> {
                    throw new NotFoundException("Employee not found, Id : " + userId);
                })
                .block();

        return employeeDto;
    }

    @Override
    public UserDto saveUser(UserDto user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            return mapper.toDto(userRepository.save(mapper.toEntity(user)));
        }
        throw new NotFoundException("Username already exist. Username : " + user.getUsername());
    }

    @Override
    public void updateUser(UserDto user) {
        User byUsername = userRepository.findByUsername(user.getUsername());
        if (byUsername == null) {
            throw new NotFoundException("User not found with username " + user.getUsername());
        }

        if (!user.getPassword().isBlank() && user.getPassword() != null) {
            byUsername.setPassword(user.getPassword());
        }
        userRepository.save(byUsername);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return mapper.toDto(userRepository.findAll());
    }

}
