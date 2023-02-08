package com.company.repository;

import com.company.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
