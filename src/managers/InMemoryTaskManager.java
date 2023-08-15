package managers;

import exceptions.TimeValidateException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected HistoryManager historyManager;
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
        if (task == null) {
            return null;
        }
        timeValidate(task);
        int id = getStaticId();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
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
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.remove(taskId));
            historyManager.remove(taskId);
        }
    }

    // methods for subtask
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllSubTasks() {
        for (SubTask task : subTasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
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
        if (task == null) {
            return null;
        }
        timeValidate(task);
        int id = getStaticId();
        task.setId(id);
        subTasks.put(id, task);
        int epicId = task.getEpicId();
        epics.get(epicId).addSubTaskId(id);
        setEpicStatus(epicId);
        setEpicTimeValues(epicId);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        int subTaskId = task.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, task);
            setEpicStatus(task.getEpicId());
            setEpicTimeValues(task.getEpicId());
        } else {
            return null;
        }
        return task;
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            prioritizedTasks.remove(subTasks.get(id));
            int epicId = subTasks.remove(id).getEpicId();
            epics.get(epicId).deleteSubTaskId(id);
            setEpicStatus(epicId);
            setEpicTimeValues(epicId);
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
        for (SubTask task : subTasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
        if (task == null) {
            return null;
        }
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
            if (task.getStartTime() != null) {
                setEpicTimeValues(epicId);
            }
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
                prioritizedTasks.remove(subTasks.remove(subTaskId));
            }
            historyManager.remove(taskId);
            epics.remove(taskId);
        }
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
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int getStaticId() {
        staticId++;
        return staticId;
    }

    private void timeValidate(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        boolean isFree = prioritizedTasks.stream()
                .allMatch(t -> start.isAfter(t.getEndTime())
                        || end.isBefore(t.getStartTime()));
        if (!isFree) {
            throw new TimeValidateException("Это время занято другой задачей!");
        }
    }

    private void setEpicStatus(int id) {
        Epic epic = epics.get(id);
        Set<Integer> subTaskId = epic.getSubTaskId();
        boolean isAllNew = subTaskId.stream().map(subId -> subTasks.get(subId).getStatus())
                .allMatch(s -> s == TaskStatus.NEW);
        boolean isAllDone = subTaskId.stream().map(subId -> subTasks.get(subId).getStatus())
                .allMatch(s -> s == TaskStatus.DONE);
        if (subTaskId.isEmpty() || isAllNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void setEpicTimeValues(int id) {
        Epic epic = epics.get(id);
        Set<Integer> subTaskId = epic.getSubTaskId();

        List<LocalDateTime> startTimeList = subTaskId.stream()
                .filter(sId -> subTasks.get(sId).getStartTime() != null)
                .map(sId -> subTasks.get(sId).getStartTime())
                .sorted()
                .collect(Collectors.toList());
        epic.setStartTime(startTimeList.get(0));

        List<LocalDateTime> endTimeList = subTaskId.stream()
                .filter(sId -> subTasks.get(sId).getEndTime() != null)
                .map(sId -> subTasks.get(sId).getEndTime())
                .sorted()
                .collect(Collectors.toList());
        epic.setEndTime(endTimeList.get(endTimeList.size() - 1));


        Duration sumSubTasksDuration = subTaskId.stream()
                .filter(idSubTask -> subTasks.get(idSubTask).getDuration() != null)
                .map(idSubTask -> subTasks.get(idSubTask).getDuration())
                .reduce(Duration.ofMinutes(0), Duration::plus);
        epic.setDuration(sumSubTasksDuration);
    }
}
