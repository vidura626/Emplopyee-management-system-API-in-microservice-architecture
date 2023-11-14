package com.qcomit.EmployeemanegementsystemAPI.service.impl;

import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Contact;
import com.qcomit.EmployeemanegementsystemAPI.repository.EmployeeRepository;
import com.qcomit.EmployeemanegementsystemAPI.service.ReportService;
import com.qcomit.EmployeemanegementsystemAPI.util.ReportEntity.EmployeeReportEntity;
import com.qcomit.EmployeemanegementsystemAPI.util.properties.StorageProperties;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final StorageProperties storageProperties;

    @Autowired
    public ReportServiceImpl(EmployeeRepository employeeRepository, StorageProperties storageProperties) {
        this.employeeRepository = employeeRepository;
        this.storageProperties = storageProperties;
    }


    @Override
    public String exportReport(String reportType) throws FileNotFoundException, JRException {
        List<EmployeeReportEntity> collect = employeeRepository.findAll().stream()
                .map(employee -> {
                    return EmployeeReportEntity.builder()
                            .contacts(employee.getContacts().stream()
                                    .map(Contact::toString)
                                    .collect(Collectors.joining("\n")))
                            .modified(employee.getModified()==null?"Not Modified":employee.getModified().toString())
                            .created(employee.getCreated().toString())
                            .address(employee.getAddress().toString())
                            .email(employee.getEmail())
                            .birthday(new SimpleDateFormat("yyyy-MM-dd").format(employee.getBirthday()))
                            .name(employee.getName().toString())
                            .employeeId(String.valueOf(employee.getEmployee_id()))
                            .profileImage(employee.getProfileImage())
                            .salary(employee.getSalary())
                            .build();
                }).toList();
        String path = storageProperties.getReports();
//        Load and compile
        File file = ResourceUtils.getFile("classpath:reports/EmployeeReport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(collect);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        if (reportType.equalsIgnoreCase("html"))
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "employees.html");
        if (reportType.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "employees.pdf");
        }
        return "Report Generated in path :" + path;
    }
}
