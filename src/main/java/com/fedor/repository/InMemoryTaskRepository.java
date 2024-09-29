package com.fedor.repository;

import com.fedor.enumerator.Status;
import com.fedor.exception.TaskNotFoundException;
import com.fedor.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks;
    private int lastId;

    public InMemoryTaskRepository() {
        this.tasks = new ArrayList<>();
        lastId = 0;
    }

    @Override
    public Task get(int id) {
        return tasks
                .stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }

    @Override
    public Task add(Task task) {
        task.setId(++lastId);
        tasks.add(task);

        return task;
    }

    @Override
    public void update(Task task) {
        tasks
                .stream()
                .filter(x -> x.getId() == task.getId())
                .findFirst()
                .map(savedTask -> {
                    savedTask.setDescription(task.getDescription());
                    savedTask.setStatus(task.getStatus());
                    savedTask.setUpdatedAt(LocalDateTime.now());
                    return task;
                });
    }

    @Override
    public void delete(int id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id);

        if (!removed)
            throw new TaskNotFoundException("Task with ID " + id + " not found");
    }

    @Override
    public List<Task> list() {
        return tasks
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> listByStatus(Status status) {
        return tasks
                .stream()
                .filter(task -> task.getStatus().equals(status))
                .sorted(Comparator.comparing(Task::getId))
                .collect(Collectors.toList());
    }
}
