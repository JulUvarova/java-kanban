package tasks;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTaskId = new HashSet<>();
    private TaskStatus status = TaskStatus.NEW;
    private final TaskType taskType;
    private int id;

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
    }

    public Epic(int id, String name, String description, HashSet<Integer> subTaskId, TaskStatus status) {
        super(name, description);
        this.id = id;
        this.subTaskId = subTaskId;
        this.status = status;
        this.taskType = TaskType.EPIC;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public HashSet<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setSubTaskId(int iD) {
        subTaskId.add(iD);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "{" + getTaskType() +
                " id=" + getID() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", subTaskId=" + getSubTaskId() +
                ", status=" + getStatus() +
                '}';
    }
}
