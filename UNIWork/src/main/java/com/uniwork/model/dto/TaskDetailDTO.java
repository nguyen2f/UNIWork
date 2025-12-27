package com.uniwork.model.dto;

import com.uniwork.model.entity.Comment;
import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailDTO {
    private TaskDTO task;
    private List<TaskDTO> childTasks;
//    private List<CommentDTO> comments;
    private List<FileAttachment> fileAttachments;
}
