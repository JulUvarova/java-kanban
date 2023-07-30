package tasks;

public class Task {
    private String name;
    private String description;
    private TaskStatus status;
    private TaskType taskType;
    private int id;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(String.valueOf(status));
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(String.valueOf(status));
        this.taskType = TaskType.TASK;
    }

        public void setID (int id){
            this.id = id;
        }

        public int getID () {
            return id;
        }


        public String getName () {
            return name;
        }

        public TaskStatus getStatus () {
            return status;
        }

        public void setName (String name){
            this.name = name;
        }

        public void setStatus (String status){
            this.status = TaskStatus.valueOf(status);
        }

        public String getDescription () {
            return description;
        }

        public void setDescription (String description){
            this.description = description;
        }

        public TaskType getTaskType () {
            return taskType;
        }

        @Override
        public String toString () {
            return "{" + getTaskType() +
                    " id=" + getID() +
                    ", name=" + getName() +
                    ", description=" + getDescription() +
                    ", status=" + getStatus() +
                    '}';
        }
    }
