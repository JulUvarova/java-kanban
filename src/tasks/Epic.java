package tasks;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {

    private Set<Integer> subTaskId = new HashSet<>();
    private TaskStatus status = TaskStatus.NEW;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Set<Integer> subTaskId, TaskStatus status) {
        super(id, name, description, status);
        this.subTaskId = subTaskId;
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
    public String toString() {
        return "{" + getTaskType() +
                " id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", subTaskId=" + getSubTaskId() +
                ", status=" + getStatus() +
                '}';
    }
}
