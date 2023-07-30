package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // methods for usual task
    void getAllTasks();

    void clearAllTasks();

    void getTaskById(int taskId);

    void addTask(Task task);

    void updateTask(int iD, String name, String description, TaskStatus status);

    void deleteTaskById(int taskId);

    // methods for subtask
    void getAllSubTasks();

    void clearAllSubTasks();

    void getSubTaskById(int taskId);

    void addSubTask(SubTask task, int epicID);

    void updateSubTask(int iD, String name, String description, TaskStatus status);

    void deleteSubTaskById(int taskId);

    // methods for epic
    void getAllEpics();

    void clearAllEpics();

    void getEpicById(int taskId);

    void addEpic(Epic task);

    void updateEpic(int iD, String name, String description);

    void deleteEpicById(int taskId);

    ArrayList<SubTask> getSubTaskByEpic(int epicId);

    // other
    List<Task> getHistory();
}

