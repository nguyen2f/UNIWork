package com.uniwork.modules.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TypingEvent {
    private Long roomId;
    private Long userId;
    private String userName;
    private boolean typing;
}

