package com.marshallchikari.taskmanager.taskservice.mapper;

import com.marshallchikari.taskmanager.taskservice.dto.TaskRequestDTO;
import com.marshallchikari.taskmanager.taskservice.dto.TaskResponseDTO;
import com.marshallchikari.taskmanager.taskservice.model.Task;

public class TaskMapper {
    public TaskResponseDTO toDto(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUserId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        return dto;
    }

    public Task toModel(TaskRequestDTO req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus());
        return task;
    }

    public void updateModel(Task task, TaskRequestDTO req) {
        if (req.getTitle() != null) task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
    }
}
