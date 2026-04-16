package com.uniwork.modules.task.service;

import com.uniwork.modules.task.dto.TaskDTO;
import com.uniwork.modules.task.dto.TaskDetailDTO;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.request.TaskRequest;

import java.util.List;

public interface TaskService {

    List<TaskDTO> getAllTasksByProjectId(Long projectId);

    TaskDetailDTO getTaskById(Long userId, Long taskId);

    List<Task> createTask(Long userId, TaskRequest taskRequest);

    Task updateTask(Long userId, Long taskId, TaskRequest taskRequest);

    Task deleteTask(Long userId, Long taskId);

    List<Task> getAllTasksByAssignedTo(Long assignedTo, Integer priority, Integer status);

}
