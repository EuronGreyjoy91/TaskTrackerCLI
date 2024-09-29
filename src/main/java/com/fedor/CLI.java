package com.fedor;

import com.fedor.enumerator.Status;
import com.fedor.model.Task;
import com.fedor.repository.JSONTaskRepository;
import com.fedor.repository.TaskRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private final Scanner scanner;
    private final TaskRepository taskRepository;
    private boolean running;

    public CLI() {
        scanner = new Scanner(System.in);
        taskRepository = new JSONTaskRepository();
        running = true;

        System.out.println("CLI Started");
    }

    public boolean isRunning() {
        return running;
    }

    public void parseCommand() {
        String fullCommand = scanner.nextLine();
        List<String> commandWords = Arrays.stream(fullCommand.split(" ")).toList();
        String action = commandWords.getFirst().toLowerCase();

        switch (action) {
            case "add" -> addTask(commandWords);
            case "update" -> updateTaskDescription(commandWords);
            case "delete" -> deleteTask(commandWords);
            case "mark-in-progress" -> updateTaskStatus(commandWords, Status.IN_PROGRESS);
            case "mark-done" -> updateTaskStatus(commandWords, Status.DONE);
            case "list" -> listTasks();
            case "list-done" -> listTasksByStatus(Status.DONE);
            case "list-todo" -> listTasksByStatus(Status.TODO);
            case "list-in-progress" -> listTasksByStatus(Status.IN_PROGRESS);
            case "help" -> showHelp();
            case "exit" -> exit();
            default -> System.out.println("Invalid command, type help to see the full list");
        }
    }

    private void addTask(List<String> commandWords) {
        String description = String.join(" ", commandWords.subList(1, commandWords.size()));

        if (description.isEmpty())
            throw new IllegalArgumentException("No description provided");

        Task taskToSave = new Task(description);
        Task savedTask = taskRepository.add(taskToSave);

        System.out.println("Task added successfully (ID: " + savedTask.getId() + ")");
    }

    private void updateTaskDescription(List<String> commandWords) {
        int id = Integer.parseInt(commandWords.get(1));

        String description = String.join(" ", commandWords.subList(2, commandWords.size()));
        if (description.isEmpty())
            throw new IllegalArgumentException("No description provided");

        Task task = taskRepository.get(id);
        task.setDescription(description);

        taskRepository.update(task);
        System.out.println("Task updated successfully (ID: " + id + ")");
    }

    private void deleteTask(List<String> commandWords) {
        int id = Integer.parseInt(commandWords.get(1));
        taskRepository.delete(id);

        System.out.println("Task deleted successfully (ID: " + id + ")");
    }

    private void updateTaskStatus(List<String> commandWords, Status status) {
        int id = Integer.parseInt(commandWords.get(1));

        Task task = taskRepository.get(id);
        task.setStatus(status);

        System.out.println("Task updated successfully (ID: " + id + ")");
    }

    private void listTasks() {
        System.out.println(taskRepository.list());
    }

    private void listTasksByStatus(Status status) {
        System.out.println(taskRepository.listByStatus(status));
    }

    private void showHelp() {
        System.out.println("""
                Commands: 
                - add [description]
                - update [id]
                - delete [id]
                - mark-in-progress [id]
                - mark-done [id]
                - list
                - list-done
                - list-todo
                - list-in-progress
                - help
                - exit
                """);
    }

    private void exit() {
        this.running = false;
        System.out.println("CLI Stopped");
    }
}
