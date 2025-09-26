package com.marshallchikari.taskmanager.taskservice.service;

import com.marshallchikari.taskmanager.taskservice.dto.TaskRequestDTO;
import com.marshallchikari.taskmanager.taskservice.dto.TaskResponseDTO;
import com.marshallchikari.taskmanager.taskservice.mapper.TaskMapper;
import com.marshallchikari.taskmanager.taskservice.model.Task;
import com.marshallchikari.taskmanager.taskservice.repository.TaskRepository;
import com.marshallchikari.taskmanager.taskservice.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepo;
    private final JwtUtil jwtUtil;

    public TaskService(TaskMapper taskMapper,
                       TaskRepository taskRepo,
                       JwtUtil jwtUtil) {
        this.taskMapper = taskMapper;
        this.taskRepo = taskRepo;
        this.jwtUtil = jwtUtil;
    }

    public TaskResponseDTO createTask(TaskRequestDTO request, String authorizationHeader) {
        Long userId = resolveUserIdFromAuthorization(authorizationHeader);

        Task task = taskMapper.toModel(request);
        task.setUserId(userId);

        Task saved = taskRepo.save(task);
        return taskMapper.toDto(saved);
    }

    public List<TaskResponseDTO> findAllForCurrentUser(String authorizationHeader) {
        Long userId = resolveUserIdFromAuthorization(authorizationHeader);
        return taskRepo.findByUserId(userId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO findByIdForCurrentUser(Long id, String authorizationHeader) {
        Long userId = resolveUserIdFromAuthorization(authorizationHeader);

        Task task = taskRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found or access denied"));
        return taskMapper.toDto(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request, String authorizationHeader) {
        Long userId = resolveUserIdFromAuthorization(authorizationHeader);

        Task existing = taskRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found or access denied"));

        taskMapper.updateModel(existing, request);
        Task saved = taskRepo.save(existing);
        return taskMapper.toDto(saved);
    }

    public void deleteTask(Long id, String authorizationHeader) {
        Long userId = resolveUserIdFromAuthorization(authorizationHeader);

        Task existing = taskRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found or access denied"));

        taskRepo.delete(existing);
    }

    private Long resolveUserIdFromAuthorization(String authorizationHeader) {
        Long userId = jwtUtil.extractUserIdFromAuthHeader(authorizationHeader);
        if (userId == null) {
            throw new IllegalArgumentException("Missing or invalid Authorization header / token");
        }
        return userId;
    }
}
