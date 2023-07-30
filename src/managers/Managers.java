package managers;

import java.nio.file.Files;

public class Managers {
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

