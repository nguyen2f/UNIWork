package com.uniwork.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomDTO {
    private Long roomId;
    private String type;
    private String name;
    private LocalDateTime createdAt;
}

