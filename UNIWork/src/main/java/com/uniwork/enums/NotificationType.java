package com.uniwork.enums;

public enum NotificationType {
    GROUP_ADDED("Bạn đã được thêm vào một nhóm mới."),
    MESSAGE("Bạn có một tin nhắn mới."),
    TASK_CREATED("Một nhiệm vụ mới đã được tạo."),
    TASK_ASSIGNED("Bạn vừa được giao một nhiệm vụ."),
    TASK_STATUS_CHANGED("Trạng thái nhiệm vụ đã được cập nhật."),
    ISSUE_ASSIGNED("Bạn vừa được giao một issue."),
    ISSUE_STATUS_CHANGED("Trạng thái issue đã được cập nhật."),
    PROJECT_UPDATED("Dự án đã được cập nhật."),
    PROJECT_MEMBER_ADDED("Bạn đã được thêm vào một dự án."),
    STAGE_COMPLETED("Một giai đoạn đã hoàn thành."),
    STAGE_ACTIVATED("Một giai đoạn đã được kích hoạt.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

}
