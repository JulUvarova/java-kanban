package managers;

import exceptions.TimeValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.TaskStatus;

import java.util.List;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest<T extends TaskManager> {
    static TaskManager manager;
    static Task task1;
    static Task task2;
    static Task taskWrongTime;
    static Task nullTask;
    static Epic epic1;
    static Epic epic2NoSubTask;
    static Epic nullEpic;
    static SubTask subTask1e1;
    static SubTask subTask2e1;
    static SubTask subTaskWrongTime;
    static SubTask nullSubTask;

    @BeforeEach
    void getManager() {
        manager = Managers.getDefaultTaskManager();
        task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0),
                Duration.ofMinutes(60));
        taskWrongTime = new Task("Ошибочная", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 8, 13, 19, 30),
                Duration.ofMinutes(60));
        nullTask = null;
        epic1 = new Epic("ПервыйПолный", "");
        epic2NoSubTask = new Epic("ВторойБезПодзадач", "");
        nullEpic = null;
        subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0),
                Duration.ofMinutes(60));
        subTaskWrongTime = new SubTask("Ошибочная", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 8, 13, 19, 30),
                Duration.ofMinutes(60));
        nullSubTask = null;
    }

    // for task
    @Test
    void addTaskInEmptyMapAndGetTaskAllTask() {
        manager.addTask(task1);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addTaskWithStandardBehaviorAndGetAllTask() {
        manager.addTask(task1);
        manager.addTask(task2);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addTaskWithWrongStartTime() {
        manager.addTask(task1);
        TimeValidateException exc = assertThrows(
                TimeValidateException.class,
                () -> manager.addTask(taskWrongTime));

        assertEquals("Это время занято другой задачей!", exc.getMessage());
    }

    @Test
    void addNullTask() {
        manager.addTask(nullTask);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void getAllTasksInEmptyMap() {
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateTaskAndGetItById() {
        final int taskId = manager.addTask(task1).getId();
        Task task1Upd = new Task(taskId, "Первая", "обновленная", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        final Task updateTask = manager.updateTask(task1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(task1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongTask() {
        manager.addTask(task1);
        Task task1Upd = new Task(2, "Первая", "обновленная", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        final Task updateTask = manager.updateTask(task1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getTaskByIdAsStandardBehavior() {
        final int id = manager.addTask(task1).getId();
        final Task taskById = manager.getTaskById(id);

        assertEquals(task1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getTaskByWrongId() {
        manager.addTask(task1);
        final Task taskById = manager.getTaskById(2);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteTaskByIdAndGetAllTasks() {
        manager.addTask(task1);
        int id = manager.addTask(task2).getId();
        manager.deleteTaskById(id);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTaskByWrongId() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.deleteTaskById(3);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllTaskInEmptyMap() {
        manager.clearAllTasks();
        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Мапа не возвращается.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllTaskInNonEmptyMap() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.clearAllTasks();
        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи нe возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    // for subTask
    @Test
    void addSubTaskInEmptyMapAndGetTaskAllTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask1e1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubTaskWithStandardBehaviorAndGetAllTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        manager.addSubTask(subTask2e1);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask2e1, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addSubTaskWithWrongStartTime() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        TimeValidateException exc = assertThrows(
                TimeValidateException.class,
                () -> manager.addSubTask(subTaskWrongTime));

        assertEquals("Это время занято другой задачей!", exc.getMessage());
    }

    @Test
    void addNullSubTask() {
        manager.addSubTask(nullSubTask);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void getAllSubTasksInEmptyMap() {
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateSubTaskAndGetItById() {
        manager.addEpic(epic1);
        final int taskId = manager.addSubTask(subTask1e1).getId();
        SubTask task1Upd = new SubTask(taskId, "Первая", "обновленная", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        final SubTask updateTask = manager.updateSubTask(task1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(task1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongSubTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        SubTask task1Upd = new SubTask(3, "Первая", "обновленная", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        final SubTask updateTask = manager.updateSubTask(task1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getSubTaskByIdAsStandardBehavior() {
        manager.addEpic(epic1);
        final int id = manager.addSubTask(subTask1e1).getId();
        final SubTask taskById = manager.getSubTaskById(id);

        assertEquals(subTask1e1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getSubTaskByWrongId() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        final SubTask taskById = manager.getSubTaskById(100);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteSubTaskByIdAndGetAllTasks() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        int id = manager.addSubTask(subTask2e1).getId();
        manager.deleteSubTaskById(id);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteSubTaskByWrongId() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        manager.addSubTask(subTask2e1);
        manager.deleteSubTaskById(4);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllSubTaskInEmptyMap() {
        manager.clearAllSubTasks();
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertNotNull(tasks, "Мапа не возвращается.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllSubTaskInNonEmptyMap() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        manager.addSubTask(subTask2e1);
        manager.clearAllSubTasks();
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertNotNull(tasks, "Задачи нe возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    //for epic
    @Test
    void addEpicInEmptyMapAndGetTaskAllEpics() {
        manager.addEpic(epic1);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicWithStandardBehaviorAndGetAllEpics() {
        manager.addEpic(epic1);
        manager.addEpic(epic2NoSubTask);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(epic2NoSubTask, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNullEpic() {
        manager.addEpic(nullEpic);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void getAllEpicsInEmptyMap() {
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateEpicAndGetItById() {
        final int taskId = manager.addEpic(epic1).getId();
        Epic epic1Upd = new Epic(taskId, "Первая", "обновленная");
        final Epic updateTask = manager.updateEpic(epic1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(epic1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongEpic() {
        manager.addEpic(epic1);
        Epic epic1Upd = new Epic(2, "Первая", "обновленная");
        final Epic updateTask = manager.updateEpic(epic1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getEpicByIdAsStandardBehavior() {
        final int id = manager.addEpic(epic1).getId();
        final Epic taskById = manager.getEpicById(id);

        assertEquals(epic1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getEpicByWrongId() {
        manager.addEpic(epic1);
        final Epic taskById = manager.getEpicById(3);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteEpicByIdAndGetAllEpics() {
        manager.addEpic(epic1);
        int id = manager.addEpic(epic2NoSubTask).getId();
        manager.deleteEpicById(id);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpicByWrongId() {
        manager.addEpic(epic1);
        manager.addEpic(epic2NoSubTask);
        manager.deleteEpicById(3);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllEpicsInEmptyMap() {
        manager.clearAllEpics();
        final List<Epic> tasks = manager.getAllEpics();

        assertNotNull(tasks, "Мапа не возвращается.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllEpicsInNonEmptyMap() {
        manager.addEpic(epic1);
        manager.addEpic(epic2NoSubTask);
        manager.clearAllEpics();
        final List<Epic> tasks = manager.getAllEpics();

        assertNotNull(tasks, "Задачи нe возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    // for other
    @Test
    void getHistoryInDifferentCase() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        manager.addSubTask(subTask2e1);
        List<Task> history = manager.getHistory();

        assertNotNull(history, "История не возвращвется.");
        assertEquals(0, history.size(), "Неверное количество задач.");

        manager.getEpicById(1);
        manager.getSubTaskById(2);
        List<Task> historyNoEmpty = manager.getHistory();

        assertNotNull(historyNoEmpty, "История не возвращвется.");
        assertEquals(2, historyNoEmpty.size(), "Неверное количество задач.");
    }

    @Test
    void getPrioritizedTasksInDifferentCase() {
        List<Task> prioritizedTasksIsEmpty = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasksIsEmpty, "История не возвращвется.");
        assertEquals(0, prioritizedTasksIsEmpty.size(), "Неверное количество задач.");

        manager.addTask(task1);
        manager.addTask(task2);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks, "История не возвращвется.");
        assertEquals(2, prioritizedTasks.size(), "Неверное количество задач.");

        manager.deleteTaskById(1);
        List<Task> prioritizedTasksAfterDeleteTask = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasksAfterDeleteTask, "История не возвращвется.");
        assertEquals(1, prioritizedTasksAfterDeleteTask.size(), "Неверное количество задач.");
    }
}