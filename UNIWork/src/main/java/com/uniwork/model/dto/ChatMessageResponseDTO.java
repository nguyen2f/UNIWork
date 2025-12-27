package com.uniwork.model.dto;

import com.uniwork.model.projection.MessageProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDTO from(MessageProjection message) {
        return ChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .senderName(message.getSenderName())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
