package com.uniwork.model.projection;

import java.time.LocalDateTime;

public interface MessageProjection {
    Long getId();
    Long getRoomId();
    String getContent();
    LocalDateTime getCreatedAt();
    String getSenderName();
    Long getSenderId();

}
