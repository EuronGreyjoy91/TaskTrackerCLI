package com.fedor.repository;

import com.fedor.enumerator.Status;
import com.fedor.model.Task;

import java.util.List;

public interface TaskRepository {
    Task get(int id);

    Task add(Task task);

    void update(Task task);

    void delete(int id);

    List<Task> list();

    List<Task> listByStatus(Status status);
}
