package com.uniwork.modules.event.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private String title;
    private Long projectId;
    private LocalDateTime date;
    private String duration;
    private String type;
//    private List<Long> attendees;
    private String location;
    private Integer priority;
    private Long createdBy;
}
