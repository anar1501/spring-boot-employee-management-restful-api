package com.company.repository;

import com.company.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByEmailorusername(String username);
    Optional<User> findUserByEmailorusername(String emailOrUsername);
}
