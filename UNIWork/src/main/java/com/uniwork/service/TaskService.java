package com.uniwork.service;

import com.uniwork.model.dto.TaskDTO;
import com.uniwork.model.dto.TaskDetailDTO;
import com.uniwork.model.entity.Task;
import com.uniwork.model.request.TaskRequest;

import java.util.List;

public interface TaskService {

    List<TaskDTO> getAllTasksByProjectId(Long projectId);

    TaskDetailDTO getTaskById(Long userId, Long taskId);

    List<Task> createTask(Long userId, TaskRequest taskRequest);

    Task updateTask(Long userId, Long taskId, TaskRequest taskRequest);

    Task deleteTask(Long userId, Long taskId);

    List<Task> getAllTasksByAssignedTo(Long assignedTo, Integer priority, Integer status);

}
