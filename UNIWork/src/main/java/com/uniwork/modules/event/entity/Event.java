package com.uniwork.modules.event.entity;

import com.uniwork.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@SQLRestriction("is_deleted = false")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private String title;
    private Long projectId;
    private LocalDateTime date;
    private String duration;
    private String type;
//    private List<Long> attendees;
    private String location;
    private Priority priority;
    private Long createdBy;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;

}
