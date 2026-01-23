package com.uniwork.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uniwork.model.enumuration.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ProfileDTO {
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String bio;
    private String address;
    private String department;
    private Boolean active;
    private String systemRole;
    private String avatarUrl;
    private String avatarPublicId;
}
