package com.uniwork.interceptors;

import com.uniwork.model.enumuration.SystemRole;
import lombok.Data;

@Data
public class Payload {
    private String token;
    private Long userId;
//    private SystemRole role;
}
