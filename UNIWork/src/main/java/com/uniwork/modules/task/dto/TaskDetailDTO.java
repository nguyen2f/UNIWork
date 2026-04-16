package com.uniwork.modules.task.dto;

import com.uniwork.modules.comment.entity.Comment;
import com.uniwork.modules.file.entity.FileAttachment;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.comment.dto.CommentDTO;
import com.uniwork.modules.file.dto.FileAttachmentDTO;
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
    private List<CommentDTO> comments;
    private List<FileAttachmentDTO> fileAttachments;
}
