package com.uniwork.enums;

public enum NotificationType {
    GROUP_ADDED("You have been added to a new group."),
    MESSAGE("You have a new message."),
    TASK_CREATED("A new task has been created."),
    TASK_ASSIGNED("You have been assigned a task."),
    TASK_STATUS_CHANGED("The task status has been updated."),
    ISSUE_ASSIGNED("You have been assigned an issue."),
    ISSUE_STATUS_CHANGED("The issue status has been updated."),
    PROJECT_UPDATED("The project has been updated."),
    PROJECT_MEMBER_ADDED("You have been added to a project."),
    STAGE_COMPLETED("A stage has been completed."),
    STAGE_ACTIVATED("A stage has been activated.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

}
