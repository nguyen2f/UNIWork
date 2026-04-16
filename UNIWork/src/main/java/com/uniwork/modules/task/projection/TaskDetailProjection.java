package com.uniwork.modules.task.projection;

import java.time.LocalDateTime;

public interface TaskDetailProjection {

    Long getTaskId();
    Long getProjectId();
    Long getAssignedTo();
    Long getCreatedBy();

    String getTitle();
    String getDescription();

    String getPriority();   // enum → String
    String getStatus();     // enum → String

    LocalDateTime getDueDate();
    LocalDateTime getCreatedDate();
    LocalDateTime getUpdatedDate();

    Boolean getCompleted();
    String getTags();

    // parent task
    Long getTaskParentId();

    // từ bảng users
    String getAssigneeName();

    // Stage info
    Long getStageId();
    String getStageName();

}
