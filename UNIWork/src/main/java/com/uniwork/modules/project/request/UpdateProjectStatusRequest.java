package com.uniwork.modules.project.request;

import lombok.Data;

@Data
public class UpdateProjectStatusRequest {
    private Integer status; // ProjectStatus code: 0=PLANNING, 1=IN_PROGRESS, 2=ON_HOLD, 3=COMPLETED, 4=CANCELLED
}
