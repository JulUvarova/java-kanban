package managers.taskManagers;

import server.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import tasks.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public static final String TASK_KEY = "task";
    public static final String SUBTASK_KEY = "subtask";
    public static final String EPIC_KEY = "epic";
    public static final String HISTORY_KEY = "history";


    public HttpTaskManager(String uriToKVServer) {
        super(null);
        this.client = new KVTaskClient(uriToKVServer);
    }

    public void load() {
        int maxId = 0;

        String strTasks = client.load(TASK_KEY);
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> listTask = gson.fromJson(strTasks, taskType);
        for (Task task : listTask) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        String strEpics = client.load(EPIC_KEY);
        Type epicType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> listEpics = gson.fromJson(strEpics, epicType);
        for (Epic task : listEpics) {
            epics.put(task.getId(), task);
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        String strSubTasks = client.load(SUBTASK_KEY);
        Type subtaskType = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> listSubtasks = gson.fromJson(strSubTasks, subtaskType);
        for (SubTask task : listSubtasks) {
            subTasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        staticId = maxId;

        String strHistory = client.load(HISTORY_KEY);
        Type historyType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        List<Integer> listHistory = gson.fromJson(strHistory, historyType);
        for (int id : listHistory) {
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (subTasks.containsKey(id)) {
                historyManager.add(subTasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            }
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put(TASK_KEY, jsonTasks);

        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put(EPIC_KEY, jsonEpics);

        String jsonSubTasks = gson.toJson(new ArrayList<>(subTasks.values()));
        client.put(SUBTASK_KEY, jsonSubTasks);

        List<Integer> historyId = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        String jsonHistory = gson.toJson(historyId);
        client.put(HISTORY_KEY, jsonHistory);
    }
}
