package com.qcomit.EmployeemanegementsystemAPI.repository;

import com.qcomit.EmployeemanegementsystemAPI.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    Employee findByUserId(Long userId);
    @Transactional
    @Modifying
    @Query(value = "update Employee e set e.profileImage = ?1 where e.employee_id = ?2")
    void saveProfilePicture(String filePath, Long employeeId);
}
