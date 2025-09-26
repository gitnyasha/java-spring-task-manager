package com.marshallchikari.taskmanager.authservice.repository;

import java.util.Optional;

import com.marshallchikari.taskmanager.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
