package com.uniwork.modules.project.dto;

import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectStatus;
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
