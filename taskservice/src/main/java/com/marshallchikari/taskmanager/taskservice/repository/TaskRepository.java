package com.marshallchikari.taskmanager.taskservice.repository;

import com.marshallchikari.taskmanager.taskservice.enums.TaskStatus;
import com.marshallchikari.taskmanager.taskservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
