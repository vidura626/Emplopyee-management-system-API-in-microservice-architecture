package com.qcomit.EmployeemanegementsystemAPI.service;

import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;

public interface ReportService {
    public String exportReport(String reportType) throws FileNotFoundException, JRException;
}
