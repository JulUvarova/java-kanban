package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.List;

public interface TaskManager {
    // methods for usual task
    List<Task> getAllTasks();

    void clearAllTasks();

    Task getTaskById(int taskId);

    Task addTask(Task task);

    Task updateTask(Task task);

    void deleteTaskById(int taskId);

    // methods for subtask
    List<SubTask> getAllSubTasks();

    void clearAllSubTasks();

    SubTask getSubTaskById(int taskId);

    SubTask addSubTask(SubTask task);

    SubTask updateSubTask(SubTask task);

    void deleteSubTaskById(int taskId);

    // methods for epic
    List<Epic> getAllEpics();

    void clearAllEpics();

    Epic getEpicById(int taskId);

    Epic addEpic(Epic task);

    Epic updateEpic(Epic task);

    void deleteEpicById(int taskId);

    List<SubTask> getSubTaskByEpic(int epicId);

    // other
    List<Task> getHistory();
}

