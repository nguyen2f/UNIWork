package com.uniwork.modules.user.entity;

import com.uniwork.enums.SystemRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@SQLRestriction("is_deleted = false")
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
//    private String department;
    private String bio;
    private Boolean active;

//    @Enumerated(EnumType.STRING)
    private String systemRole;

    private Long departmentId;

    // Avatar (Cloudinary)
    private String avatarUrl;

    private String avatarPublicId;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;
}
