package com.neocortex.taskmanager.domain;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TaskStatusManager {

    public TaskStatusManager() {
    }

    public List<Task> getCurrentTasks(User user) {
        List<Task> currentTasks = new ArrayList<>();

        user.getTasks().stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS)
                .sorted(Comparator.comparing(Task::getDueDate))
                .forEach(currentTasks::add);

        return currentTasks;
    }

    public List<Task> getCompletedTasks(User user) {
        List<Task> completedTasks = new ArrayList<>();

        user.getTasks().stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.COMPLETED)
                .sorted(Comparator.comparing(Task::getDueDate))
                .forEach(completedTasks::add);

        return completedTasks;
    }

    public List<Task> getExpiredTasks(User user) {
        List<Task> expiredTasks = new ArrayList<>();

        user.getTasks().stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.EXPIRED)
                .sorted(Comparator.comparing(Task::getDueDate))
                .forEach(expiredTasks::add);

        return expiredTasks;
    }

}
