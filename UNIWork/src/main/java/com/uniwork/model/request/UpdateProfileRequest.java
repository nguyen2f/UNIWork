package com.uniwork.model.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String department;
    private String bio;
}
