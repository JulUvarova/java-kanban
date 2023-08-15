package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, TaskStatus status, int epicId,
                   LocalDateTime startTime, Duration duration) { // for new subTask
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, TaskStatus status, int epicId,
                   LocalDateTime startTime, Duration duration) { // for fileBacked
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        epicId = id;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "{" + getTaskType() +
                " id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", epic=" + getEpicId() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
