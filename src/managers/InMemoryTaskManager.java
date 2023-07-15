package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HistoryManager historyManager;

    private static int staticId = 0;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistoryManager();
    }

    public static int getID() {
        staticId++;
        return staticId;
    }

// methods for usual task
    @Override
    public String getAllTasks() {
        return tasks.toString();
    }

    @Override
    public String clearAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        return String.valueOf(tasks);
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(taskId, task);
        return task;
    }

    @Override
    public Task addTask(Task task) {
        int id = getID();
        tasks.put(id, task);
        return tasks.get(id);
    }

    @Override
    public Task updateTask(int iD, String name, String description, TaskStatus status) {
        Task task = new Task(name, description, status);
        tasks.put(iD, task);
        return tasks.get(iD);
    }

    @Override
    public String deleteTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
        return String.valueOf(tasks.getOrDefault(taskId, null));
    }

    // methods for subtask
    @Override
    public String getAllSubTasks() {
        return subTasks.toString();
    }

    @Override
    public String clearAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        return String.valueOf(subTasks);
    }

    @Override
    public Task getSubTaskById(int taskId) {
        SubTask task = subTasks.get(taskId);
        historyManager.add(taskId, task);
        return task;
    }

    @Override
    public Task addSubTask(SubTask task, int epicID) {
        int id = getID();
        subTasks.put(id, task);
        epics.get(epicID).setSubTaskId(id);
        return subTasks.get(id);
    }

    @Override
    public Task updateSubTask(int iD, String name, String description, TaskStatus status) {
        SubTask task = new SubTask(name, description, status);
        subTasks.put(iD, task);
        return subTasks.get(iD);
    }

    @Override
    public String deleteSubTaskById(int taskId) {
        subTasks.remove(taskId);
        historyManager.remove(taskId);
        return String.valueOf(subTasks.getOrDefault(taskId, null));
    }

// methods for epic
    @Override public String getAllEpics() {
        return epics.toString();
    }

    @Override
    public String clearAllEpics() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
        return String.valueOf(epics);
    }

    @Override
    public Epic getEpicById(int taskId) {
        Epic task = epics.get(taskId);
        historyManager.add(taskId, task);
        return task;
    }

    @Override
    public Epic addEpic(Epic task) {
        int id = getID();
        epics.put(id, task);
        return epics.get(id);
    }

    @Override
    public Epic updateEpic(int iD, String name, String description) {
        List<Integer> subId = epics.get(iD).getSubTaskId();
        List<TaskStatus> subStatus = new ArrayList<>();
        for (int i = 0; i < subId.size(); i++) {
            SubTask sub = subTasks.get(subId.get(i));
            if (sub != null) {
                subStatus.add(sub.getStatus());
            }
        }
        TaskStatus status;
        boolean isAllNew = subStatus.stream().allMatch(s -> s == TaskStatus.NEW);
        boolean isAllDone = subStatus.stream().allMatch(s -> s == TaskStatus.DONE);
        if (subStatus.isEmpty() || isAllNew) {
            status = TaskStatus.NEW;
        } else if (isAllDone) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
        Epic updateEpic = new Epic(name, description, subId, status);
        epics.put(iD, updateEpic);
        return epics.get(iD);
    }

    @Override
    public String deleteEpicById(int taskId) {
       List<Integer> subEpicId = List.copyOf(epics.get(taskId).getSubTaskId());
        for (Integer x: subEpicId) {
            historyManager.remove(x);
            subTasks.remove(x);
        }
        historyManager.remove(taskId);
        epics.remove(taskId);

        return String.valueOf(epics.getOrDefault(taskId, null));
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(int epicId) {
        List<Integer> subTaskIdByEpic = epics.get(epicId).getSubTaskId();
        List<SubTask> subTaskByEpic = new ArrayList<>();
        for (int i = 0; i < subTaskIdByEpic.size(); i++) {
            SubTask sub = subTasks.get(subTaskIdByEpic.get(i));
            subTaskByEpic.add(sub);
        }
            return (ArrayList<SubTask>) subTaskByEpic;
    }

    //others
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
