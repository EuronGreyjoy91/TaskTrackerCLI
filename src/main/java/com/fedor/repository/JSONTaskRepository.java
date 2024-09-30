package com.fedor.repository;

import com.fedor.enumerator.Status;
import com.fedor.exception.TaskNotFoundException;
import com.fedor.model.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JSONTaskRepository implements TaskRepository {

    private static final String FILE_NAME = "tasks.json";
    private int lastId;

    public JSONTaskRepository() {
        try {
            File myObj = new File(FILE_NAME);
            if (myObj.createNewFile()) {
                lastId = 0;
            } else {
                List<Task> tasks = readFile();
                if (tasks.isEmpty())
                    lastId = 0;
                else
                    lastId = tasks.getLast().getId();
            }
        } catch (IOException e) {
            System.out.println("Error creating JSON file");
        }
    }

    @Override
    public Task get(int id) {
        return readFile()
                .stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }

    @Override
    public Task add(Task task) {
        task.setId(++lastId);

        List<Task> tasks = readFile();
        tasks.add(task);
        writeFile(tasks);

        return task;
    }

    @Override
    public void update(Task task) {
        List<Task> tasks = readFile();
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

        writeFile(tasks);
    }

    @Override
    public void delete(int id) {
        List<Task> tasks = readFile();
        boolean removed = tasks.removeIf(task -> task.getId() == id);

        if (!removed)
            throw new TaskNotFoundException("Task with ID " + id + " not found");

        writeFile(tasks);
    }

    @Override
    public List<Task> list() {
        return readFile();
    }

    @Override
    public List<Task> listByStatus(Status status) {
        return readFile()
                .stream()
                .filter(task -> task.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    private List<Task> readFile() {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            String stringJson = json.toString().replace("[", "").replace("]", "");
            if (stringJson.isEmpty())
                return tasks;

            String[] taskObjects = stringJson.split("},");

            for (String taskObject : taskObjects) {
                taskObject = taskObject.trim();

                if (!taskObject.endsWith("}")) {
                    taskObject += "}";
                }

                int id = Integer.parseInt(taskObject.split("\"id\": ")[1].split(",")[0].trim());
                String description = taskObject.split("\"description\": \"")[1].split("\"")[0];
                String statusString = taskObject.split("\"status\": \"")[1].split("\"")[0];
                Status status = Status.valueOf(statusString.toUpperCase());

                String createdAtString = taskObject.split("\"createdAt\": \"")[1].split("\"")[0];
                LocalDateTime createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                String updatedAtString = taskObject.split("\"updatedAt\": \"")[1].split("\"")[0];
                LocalDateTime updatedAt = updatedAtString.equals("null") ? null : LocalDateTime.parse(updatedAtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                Task task = new Task(id, description, status, createdAt, updatedAt);
                tasks.add(task);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks
                .stream()
                .sorted(Comparator.comparing(Task::getId))
                .collect(Collectors.toList());
    }

    private void writeFile(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("[\n");

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                writer.write(task.toJSON());

                if (i < tasks.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
