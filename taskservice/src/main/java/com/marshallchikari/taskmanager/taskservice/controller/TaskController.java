package com.marshallchikari.taskmanager.taskservice.controller;

import com.marshallchikari.taskmanager.taskservice.dto.TaskRequestDTO;
import com.marshallchikari.taskmanager.taskservice.dto.TaskResponseDTO;
import com.marshallchikari.taskmanager.taskservice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO created = taskService.createTask(request, authorization);
        return ResponseEntity.created(URI.create("/tasks/" + created.getId()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        List<TaskResponseDTO> tasks = taskService.findAllForCurrentUser(authorization);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("id") Long id) {
        TaskResponseDTO dto = taskService.findByIdForCurrentUser(id, authorization);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO updated = taskService.updateTask(id, request, authorization);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("id") Long id) {
        taskService.deleteTask(id, authorization);
        return ResponseEntity.noContent().build();
    }
}
