package com.uniwork.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.util.Date;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Date startDate;
    private Date endDate;
    private String status;
    private Date createdDate;
    private Date updatedDate;
}
