package com.qcomit.EmployeemanegementsystemAPI.service.impl;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.entity.Employee;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Name;
import com.qcomit.EmployeemanegementsystemAPI.repository.EmployeeRepository;
import com.qcomit.EmployeemanegementsystemAPI.service.EmployeeService;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.AlreadyExistException;
import com.qcomit.EmployeemanegementsystemAPI.service.exceptions.NotFoundException;
import com.qcomit.EmployeemanegementsystemAPI.util.constants.SecurityConstant;
import com.qcomit.EmployeemanegementsystemAPI.util.mappers.Mapper;
import com.qcomit.EmployeemanegementsystemAPI.util.properties.StorageProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
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

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new AlreadyExistException("Email already exists. Email: " + employee.getEmail());
        }

        WebClient webClient = webClientBuilder.build();

        UserDto user = buildUserDetails(employee);
        UserDto userDto = saveUserDetailsInUserService(user, webClient);
        Employee entity = mapper.toEntity(employee);
        entity.setUserId(userDto.getUser_id());
        setCurrentAgeWithDays(entity);
        return employeeRepository.save(entity).getEmployee_id();

    }

    private UserDto saveUserDetailsInUserService(UserDto user, WebClient webClient) {
        return webClient.post()
                .uri("http://localhost:8082/api/v1/user")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(throwable -> {
                    throw new AlreadyExistException("Username is already exists");
                })
                .block();
    }

    private UserDto buildUserDetails(EmployeeDto employee) {
        UserDto build = UserDto.builder()
                .username(employee.getUsername())
                .role(employee.getRole())
                .build();
        if (employee.getPassword() != null && !employee.getPassword().isEmpty())
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
        WebClient webClient = webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, token).build();

        UserDto userDetails = getUserDetailsFromUserService(entity.getUserId(), webClient);
        if (userDetails == null) throw new NotFoundException("User not found with username " + employee.getUsername());
        setUpdateEntity(entity, employee, token);
        setCurrentAgeWithDays(entity);

        entity.setModified(new Date(System.currentTimeMillis()));
        Employee save = employeeRepository.save(entity);
        EmployeeDto dto = mapper.toDto(save);
        dto.setRole(userDetails.getRole());
        dto.setUsername(userDetails.getUsername());
        return dto;
    }

    private UserDto getUserDetailsFromUserService(Long id, WebClient webClient) {
        return webClient.get()
                .uri("http://localhost:8082/api/v1/user/" + id)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(throwable -> {
                    throw new NotFoundException("User not found with id " + id);
                })
                .block();
    }

    private void setUpdateEntity(Employee entity, EmployeeDto employee, String token) {
        entity.setName(Name.builder().firstName(employee.getFirstName()).lastName(employee.getLastName()).build());
        entity.setBirthday(employee.getBirthday());
        entity.setEmail(employee.getEmail());
        entity.setAddress(mapper.toEntity(employee.getAddress()));
        entity.setContacts(mapper.toContactEntity(employee.getContacts()));
    }

    @Override
    public void deleteEmployee(Long id, String token) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found. Id: " + id));

        deleteUserDetailsFromUserService(employee.getUserId(),
                webClientBuilder
                        .defaultHeader(HttpHeaders.AUTHORIZATION, token).build());

        employeeRepository.deleteById(id);
    }

    private void deleteUserDetailsFromUserService(Long userId, WebClient webClient) {
        webClient.delete()
                .uri("http://localhost:8082/api/v1/user/" + userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> {
                    throw new NotFoundException("User not found with id " + userId);
                })
                .block();
    }

    @Override
    public String uploadProfileImage(MultipartFile image, Long employeeId, String token) throws IOException {
        if (image.isEmpty()) {
            throw new NotFoundException("Image is empty");
        } else {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new NotFoundException("Employee not found. Id: " + employeeId));

            String filePath = setFilePath(employee, image, webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, token).build());
            employeeRepository.saveProfilePicture(filePath, employeeId);
            image.transferTo(new File(filePath));
            return filePath;
        }
    }

    private String setFilePath(Employee employee, MultipartFile image, WebClient webClient) throws IOException {
        if (employee.getProfileImage() != null) Files.deleteIfExists(new File(employee.getProfileImage()).toPath());
        UserDto userDetails = getUserDetailsFromUserService(employee.getUserId(), webClient);
        return storageProperties.getProfileImage() + "Profile-image-" + userDetails.getUsername()
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
    public List<EmployeeDto> findAllEmployees(String token) {

        WebClient webClient = webClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, token)
                .build();

        return performGetAll(webClient);
    }

    private List<EmployeeDto> performGetAll(WebClient webClient) {
        List userDtos = getAllFromUserAPI(webClient);

        if (userDtos == null) throw new NotFoundException("Users not found");

        List<UserDto> userDtoList = listToUserDtoList(userDtos);

        List<Employee> allEmployees = employeeRepository.findAll();

        return toEmployeeDtoList(userDtoList, allEmployees);
    }

    private List<EmployeeDto> toEmployeeDtoList(List<UserDto> userDtoList, List<Employee> allEmployees) {
        return allEmployees.stream()
                .map(employee -> {
                    EmployeeDto dto = mapper.toDto(employee);
                    (userDtoList).stream()
                            .filter(userDto -> userDto.getUser_id().equals(employee.getUserId()))
                            .findFirst()
                            .ifPresent(userDto -> {
                                dto.setUsername(userDto.getUsername());
                                dto.setRole(userDto.getRole());
                            });
                    return dto;
                }).collect(toList());
    }

    private List<UserDto> listToUserDtoList(List userDtos) {
        return ((List<LinkedHashMap<String, Object>>) userDtos).stream()
                .map(user -> UserDto.builder()
                        .user_id(Long.parseLong(user.get("user_id").toString()))
                        .username(user.get("username").toString())
                        .role(user.get("role").toString())
                        .build())
                .toList();
    }

    private List getAllFromUserAPI(WebClient webClient) {
        return webClient.get()
                .uri("http://localhost:8082/api/v1/user")
                .retrieve()
                .bodyToMono(List.class)
                .doOnError(throwable -> {
                    throw new RuntimeException("Something wend wrong!");
                })
                .block();
    }

    @Override
    public EmployeeDto findEmployeeById(Long id, String token) {
        WebClient webClient = webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        Employee entity = employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found. Id: " + id));
        UserDto userDetails = getUserDetailsFromUserService(entity.getUserId(), webClient);
        EmployeeDto dto = mapper.toDto(entity);
        dto.setUsername(userDetails.getUsername());
        dto.setRole(userDetails.getRole());
        return dto;
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
    public EmployeeDto findEmployeeByUserId(Long id) {
        Employee byUserId = employeeRepository.findByUserId(id);
        if (byUserId == null) {
            throw new NotFoundException("Employee not found. Id: " + id);
        }
        return mapper.toDto(byUserId);
    }

    private Claims extractToken(String token) {
        token = token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecurityConstant.SECRET_KEY));
        return (Claims) Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(token)
                .getPayload();
    }
}
