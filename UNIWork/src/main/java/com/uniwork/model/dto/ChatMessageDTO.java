package com.uniwork.model.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private Long roomId;
    private Long senderId;
    private String content;
}
