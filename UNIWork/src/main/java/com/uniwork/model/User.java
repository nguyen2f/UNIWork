package com.uniwork.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private Date createdDate;
    private Date updatedDate;

}
