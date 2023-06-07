import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private static int staticId = 0;

    public static int getID(){
        staticId++;
        return staticId;
    }

// methods for usual task
    public String getAllTasks() {
        return tasks.toString();
    }

    public String clearAllTasks() {
        tasks.clear();
        return String.valueOf(tasks);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Task addTask(Task task) {
        staticId = Manager.getID();
        tasks.put(staticId, task);
        return tasks.get(staticId);
    }

    public Task updateTask(int iD, String name, String description, TaskStatus status) {
        Task task = new Task(name, description, status);
        tasks.put(iD, task);
        return tasks.get(iD);
    }

    public String deleteTaskById(int taskId) {
        tasks.remove(taskId);
        return String.valueOf(tasks.getOrDefault(taskId, null));
    }

    // methods for subtask
    public String getAllSubTasks() {
        return subTasks.toString();
    }

    public String clearAllSubTasks() {
        subTasks.clear();
        return String.valueOf(subTasks);
    }

    public Task getSubTaskById(int taskId) {
        return subTasks.get(taskId);
    }

    public Task addSubTask(SubTask task, int epicID) {
        staticId = Manager.getID();
        subTasks.put(staticId, task);
        epics.get(epicID).setSubTaskId(staticId);
        return subTasks.get(staticId);
    }

    public Task updateSubTask(int iD, String name, String description, TaskStatus status) {
        SubTask task = new SubTask(name, description, status);
        subTasks.put(iD, task);
        return subTasks.get(iD);
    }

    public String deleteSubTaskById(int taskId) {
        //subTaskId.remove(taskId);
        subTasks.remove(taskId);
        return String.valueOf(subTasks.getOrDefault(taskId, null));
    }

// methods for epic
    public String getAllEpics() {
        return epics.toString();
    }

    public String clearAllEpics() {
        epics.clear();
        subTasks.clear();
        return String.valueOf(epics);
    }

    public Epic getEpicById(int taskId) {
        return epics.getOrDefault(taskId, null);
    }

    public Epic addEpic(Epic task) {
        staticId = Manager.getID();
        epics.put(staticId, task);
        return epics.get(staticId);
    }

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

    public String deleteEpicById(int taskId) {
        List<Integer> subEpicId = epics.get(taskId).getSubTaskId();
        for (int i = 0; i < subEpicId.size(); i++) {
            subTasks.remove(i);
        }
        epics.remove(taskId);
        return String.valueOf(epics.getOrDefault(taskId, null));
    }

    public ArrayList<SubTask> getSubTaskByEpic(int epicId) {
        List<Integer> subTaskIdByEpic = epics.get(epicId).getSubTaskId();
        List<SubTask> subTaskByEpic = new ArrayList<>();
        for (int i = 0; i < subTaskIdByEpic.size(); i++) {
            SubTask sub = subTasks.get(subTaskIdByEpic.get(i));
            subTaskByEpic.add(sub);
        }
            return (ArrayList<SubTask>) subTaskByEpic;
    }
}
