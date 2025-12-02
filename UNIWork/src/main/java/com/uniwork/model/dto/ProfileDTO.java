package com.uniwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String bio;
    private String address;
    private String department;
    private Boolean active;
}
