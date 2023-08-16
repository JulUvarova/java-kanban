package managers;

import exceptions.TimeValidateException;
import tasks.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected HistoryManager historyManager;
    private Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);
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
        Task oldTask = tasks.get(taskId);
        if (oldTask != null) {
            timeValidate(task);
            prioritizedTasks.remove(oldTask);
            prioritizedTasks.add(task);
            tasks.put(taskId, task);
            return task;
        } else {
            return null;
        }
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
            epic.setDuration(0);
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
        SubTask oldTask = subTasks.get(subTaskId);
        if (oldTask != null) {
            timeValidate(task);
            prioritizedTasks.remove(oldTask);
            prioritizedTasks.add(task);
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
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
            int epicId = subTask.getEpicId();
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
            setEpicTimeValues(epicId);
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

    @Override
    public int getLastStaticId() {
        return staticId;
    }

    private int getStaticId() {
        staticId++;
        return staticId;
    }

    private void timeValidate(Task task) {
        for (Task taskPriority : prioritizedTasks) {
            if (task.getStartTime() == null || taskPriority.getStartTime() == null) {
                return;
            }
            if (task.getId() == taskPriority.getId()) {
                continue;
            }
            if (task.getStartTime().isAfter(taskPriority.getEndTime())
                    || task.getEndTime().isBefore(taskPriority.getStartTime())
                    || task.getEndTime().equals(taskPriority.getStartTime())
                    || task.getStartTime().equals(taskPriority.getEndTime())
            ) {
                continue;
            } else {
                throw new TimeValidateException("Это время занято другой задачей!");
            }
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
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        long duration = 0;
        for (int subId : subTaskId) {
            SubTask subTask = subTasks.get(subId);
            duration += subTasks.get(subId).getDuration();
            if (subTask.getStartTime() != null) {
                if (startTime == null || subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }
                if (endTime == null || subTask.getEndTime().isAfter(endTime)) {
                    endTime = subTask.getEndTime();
                }
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }
}
