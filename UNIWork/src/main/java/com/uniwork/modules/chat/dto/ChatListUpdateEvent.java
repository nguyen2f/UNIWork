package com.uniwork.modules.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatListUpdateEvent {
    private Long roomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long lastMessageSenderId;
    private String lastMessageSenderName;
}
