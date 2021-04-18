package com.neocortex.taskmanager.domain;

import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Please enter a task")
    @Size(max = 60, message = "Task message is to long")
    private String taskMessage;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {
    }

    public Task(String taskMessage, LocalDate dueDate, TaskStatus taskStatus) {
        this.taskMessage = taskMessage;
        this.dueDate = dueDate;
        this.taskStatus = taskStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskMessage() {
        return taskMessage;
    }

    public void setTaskMessage(String taskMessage) {
        this.taskMessage = taskMessage;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate endDate) {
        this.dueDate = endDate;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @PostLoad
    private void checkDueDate(){
        if(LocalDate.now().isAfter(dueDate)) taskStatus = TaskStatus.EXPIRED;
    }

    @Override
    public String toString() {
        return "Task{" +
                ", taskMessage='" + taskMessage + '\'' +
                ", dueDate=" + dueDate +
                ", taskStatus=" + taskStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id) && Objects.equals(taskMessage, task.taskMessage) && Objects.equals(dueDate, task.dueDate) && taskStatus == task.taskStatus && Objects.equals(user, task.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskMessage, dueDate, taskStatus, user);
    }
}
