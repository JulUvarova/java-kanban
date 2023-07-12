package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // methods for usual task
    String getAllTasks();

    String clearAllTasks();

    Task getTaskById(int taskId);

    Task addTask(Task task);

    Task updateTask(int iD, String name, String description, TaskStatus status);

    String deleteTaskById(int taskId);

    // methods for subtask
    String getAllSubTasks();

    String clearAllSubTasks();

    Task getSubTaskById(int taskId);

    Task addSubTask(SubTask task, int epicID);

    Task updateSubTask(int iD, String name, String description, TaskStatus status);

    String deleteSubTaskById(int taskId);

    // methods for epic
    String getAllEpics();

    String clearAllEpics();

    Epic getEpicById(int taskId);

    Epic addEpic(Epic task);

    Epic updateEpic(int iD, String name, String description);

    String deleteEpicById(int taskId);

    ArrayList<SubTask> getSubTaskByEpic(int epicId);

    // other
    List<Task> getHistory();
}

