class SubTask extends Task {
    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
