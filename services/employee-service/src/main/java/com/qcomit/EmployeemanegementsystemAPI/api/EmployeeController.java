package com.qcomit.EmployeemanegementsystemAPI.api;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.service.EmployeeService;
import com.qcomit.EmployeemanegementsystemAPI.service.ReportService;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final ReportService reportService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, PasswordEncoder passwordEncoder, ReportService reportService) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
        this.reportService = reportService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable("id") String id) {
        return ResponseEntity.ok(employeeService.findEmployeeById(Long.parseLong(id)));
    }

    @GetMapping("/userId/{id}")
    public ResponseEntity<EmployeeDto> findEmployeeByUserId(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.findEmployeeByUserId(Long.parseLong(id)));
    }


    @GetMapping
    public ResponseEntity<List<EmployeeDto>> findEmployees() {
        return ResponseEntity
                .ok()
                .body(employeeService.findAllEmployees());
    }


    @GetMapping("/image")
    public ResponseEntity<EmployeeDto> findProfilePicture(@RequestParam("id") String id) {
        return null;
    }


    @PostMapping
    public ResponseEntity<String> saveEmployee(@RequestBody @Valid EmployeeDto employee) throws IOException {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return ResponseEntity.ok().body("Saved. Id: " + employeeService.saveEmployee(employee));
    }

    @PostMapping("/profileImage")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("image") MultipartFile image,
                                                       @RequestParam("id") Long id) throws IOException {
        return ResponseEntity.ok().body(employeeService.uploadProfileImage(image, id));
    }

    @GetMapping("/profileImage/{empId}")
    public ResponseEntity<?> downloadProfilePicture(@PathVariable("empId") Long empId) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(employeeService.downloadProfileImage(empId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id) {
        employeeService.deleteEmployee(Long.parseLong(id));
        return ResponseEntity.ok().body("Deleted");
    }


    @PutMapping
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody @Valid EmployeeDto employee,
                                                      @RequestParam("id") Long id,
                                                      @RequestHeader("Authorization") String token) {
        employee.setRole("ROLE_" + employee.getRole());
        if (employee.getPassword() != null) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
        return ResponseEntity.ok(employeeService.updateEmployee(employee, id, token));
    }


    @GetMapping("/reports/{type}")
    public ResponseEntity<String> exportReport(@PathVariable("type") String reportType) throws JRException, FileNotFoundException {
        return ResponseEntity.ok().body(reportService.exportReport(reportType));
    }
}
