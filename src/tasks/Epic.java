package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private LocalDateTime endTime;
    private Set<Integer> subTaskId = new HashSet<>();

    public Epic(String name, String description) { // for new Epic
        super(name, description);
        endTime = null;
    }
    public Epic(int id, String name, String description) { // for update Epic
        super(id, name, description);
        endTime = null;
    }

    public Epic(int id, String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) { // for fileBacked
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public Set<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void addSubTaskId(int id) {
        subTaskId.add(id);
    }

    public void deleteSubTaskId(int id) {
        subTaskId.remove(id);
    }

    public void clearSubtasksId() {
        subTaskId.clear();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "{" + getTaskType() +
                " id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", subTaskId=" + getSubTaskId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", epicEndTime=" + getEndTime() +
                '}';
    }
}
