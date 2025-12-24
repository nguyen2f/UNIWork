package com.uniwork.model.entity;

import com.uniwork.model.enumuration.SystemRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String address;
    private String phone;
    private String department;
    private String bio;
    private Boolean active;

    private SystemRole systemRole;
}
