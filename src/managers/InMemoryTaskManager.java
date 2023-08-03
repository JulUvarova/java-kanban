package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected HistoryManager historyManager;
    protected int staticId = 0;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistoryManager();
    }

    // methods for usual task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task addTask(Task task) {
        int id = getStaticId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        int taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        } else {
            return null;
        }
        return task;
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    // methods for subtask
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            setEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTaskById(int taskId) {
        SubTask task = subTasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask addSubTask(SubTask task) {
        int id = getStaticId();
        task.setId(id);
        subTasks.put(id, task);
        int epicId = task.getEpicId();
        epics.get(epicId).addSubTaskId(id);
        setEpicStatus(epicId);
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        int subTaskId = task.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, task);
            setEpicStatus(task.getEpicId());
        } else {
            return null;
        }
        return task;
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.remove(id).getEpicId();
            epics.get(epicId).deleteSubTaskId(id);
            setEpicStatus(epicId);
            historyManager.remove(id);
        }
    }

    // methods for epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
    public Epic getEpicById(int taskId) {
        Epic task = epics.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic addEpic(Epic task) {
        int id = getStaticId();
        task.setId(id);
        epics.put(id, task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic task) {
        int epicId = task.getId();
        if (epics.containsKey(epicId)) {
            setEpicStatus(epicId);
            epics.put(epicId, task);
        } else {
            return null;
        }
        return task;
    }

    @Override
    public void deleteEpicById(int taskId) {
        if (epics.containsKey(taskId)) {
            Set<Integer> subTasksId = epics.get(taskId).getSubTaskId();
            for (Integer subTaskId : subTasksId) {
                historyManager.remove(subTaskId);
                subTasks.remove(subTaskId);
            }
        }
        historyManager.remove(taskId);
        epics.remove(taskId);
    }

    @Override
    public List<SubTask> getSubTaskByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        Set<Integer> subTaskIdByEpic = epics.get(epicId).getSubTaskId();
        List<SubTask> subTaskByEpic = new ArrayList<>();
        for (int subNumber : subTaskIdByEpic) {
            SubTask sub = subTasks.get(subNumber);
            subTaskByEpic.add(sub);
        }
        return subTaskByEpic;
    }

    //others
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getStaticId() {
        staticId++;
        return staticId;
    }

    private void setEpicStatus(int id) {
        Epic epic = epics.get(id);
        Set<Integer> subTaskId = epic.getSubTaskId();
        List<TaskStatus> subTaskStatus = new ArrayList<>();
        for (int subNumber : subTaskId) {
            SubTask sub = subTasks.get(subNumber);
            if (sub != null) {
                subTaskStatus.add(sub.getStatus());
            }
        }
        TaskStatus status;
        boolean isAllNew = subTaskStatus.stream().allMatch(s -> s == TaskStatus.NEW);
        boolean isAllDone = subTaskStatus.stream().allMatch(s -> s == TaskStatus.DONE);
        if (subTaskStatus.isEmpty() || isAllNew) {
            status = TaskStatus.NEW;
        } else if (isAllDone) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
        epic.setStatus(status);
    }
}
