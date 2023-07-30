package tasks;

public class SubTask extends Task {
    private final TaskType taskType;
    private int id;
    private int epicId;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(int id, String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.taskType = TaskType.SUBTASK;
        this.id = id;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        epicId = id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
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
                ", status=" + getStatus() +
                ", epic=" + getEpicId() +
                '}';
    }
}
