package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected static Map<Integer, Task> tasks = new HashMap<>();
    protected static Map<Integer, Epic> epics = new HashMap<>();
    protected static Map<Integer, SubTask> subTasks = new HashMap<>();
    protected static HistoryManager historyManager;
    protected static int staticId = 0;

    public InMemoryTaskManager() {
       this.historyManager = Managers.getDefaultHistoryManager();
    }

    public static int getID() {
        staticId++;
        return staticId;
    }

    // methods for usual task
    @Override
    public void getAllTasks() {
        System.out.println(tasks);
    }

    @Override
    public void clearAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(taskId, task);
        System.out.println(task);
    }

    @Override
    public void addTask(Task task) {
        int id = getID();
        task.setID(id);
        tasks.put(id, task);
        System.out.println(task);
    }

    @Override
    public void updateTask(int iD, String name, String description, TaskStatus status) {
        Task task = new Task(name, description, status);
        tasks.put(iD, task);
        System.out.println(task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    // methods for subtask
    @Override
    public void getAllSubTasks() {
        System.out.println(subTasks);
    }

    @Override
    public void clearAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
    }

    @Override
    public void getSubTaskById(int taskId) {
        SubTask task = subTasks.get(taskId);
        historyManager.add(taskId, task);
        System.out.println(task);
    }

    @Override
    public void addSubTask(SubTask task, int epicID) {
        int id = getID();
        task.setID(id);
        subTasks.put(id, task);
        subTasks.get(id).setEpicId(epicID);
        epics.get(epicID).setSubTaskId(id);
        System.out.println(task);
    }

    @Override
    public void updateSubTask(int iD, String name, String description, TaskStatus status) {
        SubTask task = new SubTask(name, description, status);
        subTasks.put(iD, task);
        System.out.println(task);
    }

    @Override
    public void deleteSubTaskById(int taskId) {
        subTasks.remove(taskId);
        historyManager.remove(taskId);
    }

    // methods for epic
    @Override
    public void getAllEpics() {
        System.out.println(epics);
    }

    @Override
    public void clearAllEpics() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void getEpicById(int taskId) {
        Epic task = epics.get(taskId);
        historyManager.add(taskId, task);
        System.out.println(task);
    }

    @Override
    public void addEpic(Epic task) {
        int id = getID();
        task.setID(id);
        epics.put(id, task);
        List<Integer> subtaskByEpic = new ArrayList<>(task.getSubTaskId());
        for (int subNumber : subtaskByEpic) {
            subTasks.get(subNumber).setEpicId(id);
        }
        System.out.println(task);
    }

    @Override
    public void updateEpic(int id, String name, String description) {
        HashSet<Integer> subId = epics.get(id).getSubTaskId();
        List<TaskStatus> subStatus = new ArrayList<>();
        for (int subNumber : subId) {
            SubTask sub = subTasks.get(subNumber);
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
        Epic updateEpic = new Epic(id, name, description, subId, status);
        epics.put(id, updateEpic);
        System.out.println(updateEpic);
    }

    @Override
    public void deleteEpicById(int taskId) {
        List<Integer> subEpicId = List.copyOf(epics.get(taskId).getSubTaskId());
        for (Integer x : subEpicId) {
            historyManager.remove(x);
            subTasks.remove(x);
        }
        historyManager.remove(taskId);
        epics.remove(taskId);
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(int epicId) {
        HashSet<Integer> subTaskIdByEpic = epics.get(epicId).getSubTaskId();
        List<SubTask> subTaskByEpic = new ArrayList<>();
        for (int subNumber : subTaskIdByEpic) {
            SubTask sub = subTasks.get(subNumber);
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
