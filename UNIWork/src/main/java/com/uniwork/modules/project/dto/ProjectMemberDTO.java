package com.uniwork.modules.project.dto;

import com.uniwork.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectMemberDTO {
    private Long pmId;
    private Long userId;
    private String userName;
    private String email;
    private Role role;
}
