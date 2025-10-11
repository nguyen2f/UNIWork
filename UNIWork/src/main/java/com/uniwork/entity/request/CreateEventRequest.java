package com.uniwork.entity.request;

import com.uniwork.entity.enumuration.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private String title;
    private Long projectId;
    private Date date;
    private String duration;
    private String type;
//    private List<Long> attendees;
    private String location;
    private Integer priority;
    private Long createdBy;
}
