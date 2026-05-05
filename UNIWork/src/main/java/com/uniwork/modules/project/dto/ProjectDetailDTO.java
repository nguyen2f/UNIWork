package com.uniwork.modules.project.dto;

import com.uniwork.modules.stage.dto.StageDetailDTO;
import com.uniwork.modules.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDetailDTO {

    private ProjectDTO project;
    private List<UserDTO> members;
    private List<StageDetailDTO> stages;
}
