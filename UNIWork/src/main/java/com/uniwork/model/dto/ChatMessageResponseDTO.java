package com.uniwork.model.dto;

import com.uniwork.model.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDTO from(Message message) {
        return ChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
