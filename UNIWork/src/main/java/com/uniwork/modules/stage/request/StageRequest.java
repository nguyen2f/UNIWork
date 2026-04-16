package com.uniwork.modules.stage.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StageRequest {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
