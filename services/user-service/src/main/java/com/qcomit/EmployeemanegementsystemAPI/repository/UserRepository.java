package com.qcomit.EmployeemanegementsystemAPI.repository;

import com.qcomit.EmployeemanegementsystemAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);

    User findByUsername(String username);
}
