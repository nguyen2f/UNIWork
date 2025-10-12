package com.uniwork.repository;

import com.uniwork.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM User u")
    Long countAll();

    Long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
}
