package tasks;

import java.util.List;

public class Epic extends Task {

    private List<Integer> subTaskId;
    private TaskStatus status = TaskStatus.NEW;

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    public List<Integer> getSubTaskId() {
        return subTaskId;
    }

    public Epic(String name, String description, List<Integer> subTaskId) {
        super(name, description);
        this.subTaskId = subTaskId;
    }

    public Epic(String name, String description, List<Integer> subTaskId, TaskStatus status) {
        super(name, description);
        this.subTaskId = subTaskId;
        this.status = status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setSubTaskId(int iD) {
        subTaskId.add(iD);
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTaskId=" + getSubTaskId() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
