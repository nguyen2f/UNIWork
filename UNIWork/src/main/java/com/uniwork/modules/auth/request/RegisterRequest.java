package com.uniwork.modules.auth.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String department;
}
