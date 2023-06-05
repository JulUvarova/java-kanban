import java.util.List;

class Epic extends Task {
    List<Integer> subTaskId;
    TaskStatus status = TaskStatus.NEW;

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


    public void setSubTaskId(List<Integer> subTaskId) {
        this.subTaskId = subTaskId;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTaskId=" + getSubTaskId() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

}
