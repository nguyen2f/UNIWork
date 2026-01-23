package com.uniwork.model.dto;

import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {

    private Long projectId;
    private String name;
    private Priority priority;
    private ProjectStatus status;
    private LocalDateTime endDate;
}
