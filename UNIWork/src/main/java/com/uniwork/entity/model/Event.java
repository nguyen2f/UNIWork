package com.uniwork.entity.model;

import com.uniwork.entity.enumuration.Priority;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "events")
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

}
