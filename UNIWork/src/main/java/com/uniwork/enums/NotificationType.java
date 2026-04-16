package com.uniwork.enums;

public enum NotificationType {
    GROUP_ADDED("You have been added to a new group."),
    MESSAGE("You have a new message."),
    TASK_CREATED("You have a new message."),
    TASK_ASSIGNED("You have a new notification.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return  defaultMessage;
    }

}
