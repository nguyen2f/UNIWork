package com.uniwork.modules.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupDTO {
    private String name;

    private List<Long> members;
}
