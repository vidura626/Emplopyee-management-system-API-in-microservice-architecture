package com.qcomit.EmployeemanegementsystemAPI.service.impl;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.entity.Employee;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Name;
import com.qcomit.EmployeemanegementsystemAPI.repository.EmployeeRepository;
import com.qcomit.EmployeemanegementsystemAPI.service.EmployeeService;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.AlreadyExistException;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.NotFoundException;
import com.qcomit.EmployeemanegementsystemAPI.util.mappers.Mapper;
import com.qcomit.EmployeemanegementsystemAPI.util.properties.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StorageProperties storageProperties;
    private final WebClient.Builder webClientBuilder;
    private final Mapper mapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               Mapper mapper,
                               StorageProperties storageProperties, WebClient.Builder webClientBuilder) {
        this.employeeRepository = employeeRepository;
        this.storageProperties = storageProperties;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Long saveEmployee(EmployeeDto employee) throws IOException {
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            throw new NotFoundException("Role not found.");
        } else employee.setRole("ROLE_" + employee.getRole());

        WebClient webClient = webClientBuilder.build();

        UserDto user = buildUserDetails(employee);
        UserDto userDto = webClient.post()
                .uri("http://localhost:8082/api/v1/user")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(throwable -> {
                    throw new AlreadyExistException("Username is already exists");
                })
                .block();

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new AlreadyExistException("Email already exists. Email: " + employee.getEmail());
        } else {
            Employee entity = mapper.toEntity(employee);
            entity.setUserId(userDto.getUser_id());
            setCurrentAgeWithDays(entity);
            return employeeRepository.save(entity).getEmployee_id();
        }
    }

    private UserDto buildUserDetails(EmployeeDto employee) {
        UserDto build = UserDto.builder()
                .username(employee.getUsername())
                .role(employee.getRole())
                .build();
        if (employee.getPassword() != null || !employee.getPassword().isEmpty())
            build.setPassword(employee.getPassword());
        return build;
    }

    private void setCurrentAgeWithDays(Employee entity) {
        entity.setCurrent_age_in_days(
                (int) Duration.between(
                        entity.getBirthday().toInstant(),
                        new Date().toInstant()).toDays());
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employee, Long id, String token) {
        Employee entity = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found. Id: " + id));

        setUpdateEntity(entity, employee, token);
        setCurrentAgeWithDays(entity);

        entity.setModified(new Date(System.currentTimeMillis()));
        Employee save = employeeRepository.save(entity);
        return mapper.toDto(save);

    }

    private void setUpdateEntity(Employee entity, EmployeeDto employee, String token) {
        entity.setName(Name.builder().firstName(employee.getFirstName()).lastName(employee.getLastName()).build());
        entity.setBirthday(employee.getBirthday());
        entity.setEmail(employee.getEmail());

        WebClient webClient = webClientBuilder.build();
        UserDto userDto = buildUserDetails(employee);
        Boolean isUpdated = webClient.put()
                .uri("http://localhost:8082/api/v1/user")
                .header("Authorization", token)
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(throwable -> {
                    throw new NotFoundException("User not found.");
                })
                .block();

        if (Boolean.FALSE.equals(isUpdated)) {
            throw new NotFoundException("User not found.");
        }

        entity.setAddress(mapper.toEntity(employee.getAddress()));
        entity.setContacts(mapper.toContactEntity(employee.getContacts()));
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee not found. Id: " + id);
        } else employeeRepository.deleteById(id);
    }

    @Override
    public String uploadProfileImage(MultipartFile image, Long employeeId) throws IOException {
        if (image.isEmpty()) {
            throw new NotFoundException("Image is empty");
        } else {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new NotFoundException("Employee not found. Id: " + employeeId));
            String filePath = setFilePath(employee, image);
            employeeRepository.saveProfilePicture(filePath, employeeId);
            image.transferTo(new File(filePath));
            return filePath;
        }
    }

    private String setFilePath(Employee employee, MultipartFile image) throws IOException {
        if (employee.getProfileImage() != null) Files.deleteIfExists(new File(employee.getProfileImage()).toPath());

        //todo get username from token
//        employee.getUser().getUsername()
        String username = "username";
        return storageProperties.getProfileImage() + "Profile-image-" + username
                + Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf("."));
    }

    @Override
    public byte[] downloadProfileImage(Long employeeId) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID : " + employeeId));
        String profileImage = employee.getProfileImage();
        return Files.readAllBytes(new File(profileImage).toPath());
    }

    @Override
    public List<EmployeeDto> findAllEmployees() {
        return mapper.toDto(employeeRepository.findAll());
    }

    @Override
    public EmployeeDto findEmployeeById(Long id) {
        return mapper.toDto(employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found. Id: " + id)));
    }


    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshEmployeeAgeWithDays() {
        employeeRepository.findAll().forEach(employee -> {
            employee.setCurrent_age_in_days(employee.getCurrent_age_in_days() + 1);
            employeeRepository.save(employee);
        });
    }

    @Override
    public EmployeeDto findEmployeeByUserDto(UserDto user) {
        //todo : Call to user service to check the user
//        if (!userService.existsByUsername(user.getUsername())) {
//            throw new NotFoundException("User not found. Username: " + user.getUsername());
//        }
//        return mapper.toDto(employeeRepository.findEmployeeByUser(mapper.toEntity(user)));
        return null;
    }

    @Override
    public EmployeeDto findEmployeeByUserId(Long id) {
        Employee byUserId = employeeRepository.findByUserId(id);
        if (byUserId == null) {
            throw new NotFoundException("Employee not found. Id: " + id);
        }
        return mapper.toDto(byUserId);
    }
}
