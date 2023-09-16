package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.hisroryManager.HistoryManager;
import managers.hisroryManager.InMemoryHistoryManager;
import managers.taskManagers.FileBackedTasksManager;
import managers.taskManagers.HttpTaskManager;
import managers.taskManagers.InMemoryTaskManager;
import managers.taskManagers.TaskManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFileBackedTaskManager(String saveFile) {
        return new FileBackedTasksManager(saveFile);
    }

    public static TaskManager getDefaultHttpTaskManager(String uri) {
        return new HttpTaskManager(uri);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsb = new GsonBuilder();
        gsb.serializeNulls();
        return gsb.create();
    }
}

