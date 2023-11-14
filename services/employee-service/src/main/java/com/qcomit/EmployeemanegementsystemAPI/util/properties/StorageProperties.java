package com.qcomit.EmployeemanegementsystemAPI.util.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.path")
@Data
public class StorageProperties {
    private String profileImage;
    private String reports;
}
