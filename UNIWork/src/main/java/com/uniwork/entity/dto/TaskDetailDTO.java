package com.uniwork.entity.dto;

import com.uniwork.entity.model.Comment;
import com.uniwork.entity.model.FileAttachment;
import com.uniwork.entity.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailDTO {
    private Task task;
    private List<Comment> comments;
    private List<FileAttachment> fileAttachments;
}
