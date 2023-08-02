package managers;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getDefaultNewTaskManager(String saveFile) {
       return new FileBackedTasksManager(saveFile);
    }
    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}

