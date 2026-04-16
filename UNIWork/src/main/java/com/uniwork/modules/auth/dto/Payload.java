package com.uniwork.modules.auth.dto;

import com.uniwork.enums.SystemRole;
import lombok.Data;

@Data
public class Payload {

    private Long userId;
    private SystemRole role;
}
