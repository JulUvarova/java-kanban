package managers;

import exceptions.TimeValidateException;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.TaskStatus;

import java.util.List;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    // for task
    @Test
    void addTaskInEmptyMapAndGetTaskAllTask() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task1);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addTaskWithStandardBehaviorAndGetAllTask() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        manager.addTask(task1);
        manager.addTask(task2);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addTaskWithWrongStartTime() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task taskWrongTime = new Task("Ошибочная", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 8, 13, 19, 30), 60);
        manager.addTask(task1);
        TimeValidateException exc = assertThrows(
                TimeValidateException.class,
                () -> manager.addTask(taskWrongTime));

        assertEquals("Это время занято другой задачей!", exc.getMessage());
    }

    @Test
    void addNullTask() {
        manager.addTask(null);
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
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final int taskId = manager.addTask(task1).getId();
        Task task1Upd = new Task(taskId, "Первая", "обновленная", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final Task updateTask = manager.updateTask(task1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(task1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongTask() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task1);
        Task task1Upd = new Task(2, "Первая", "обновленная", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final Task updateTask = manager.updateTask(task1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getTaskByIdAsStandardBehavior() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final int id = manager.addTask(task1).getId();
        final Task taskById = manager.getTaskById(id);

        assertEquals(task1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getTaskByWrongId() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task1);
        final Task taskById = manager.getTaskById(2);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteTaskByIdAndGetAllTasks() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        manager.addTask(task1);
        int id = manager.addTask(task2).getId();
        manager.deleteTaskById(id);
        final List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTaskByWrongId() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask1e1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubTaskWithStandardBehaviorAndGetAllTask() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        manager.addSubTask(subTask2e1);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask2e1, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addSubTaskWithWrongStartTime() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTaskWrongTime = new SubTask("Ошибочная", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 8, 13, 19, 30), 60);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        TimeValidateException exc = assertThrows(
                TimeValidateException.class,
                () -> manager.addSubTask(subTaskWrongTime));

        assertEquals("Это время занято другой задачей!", exc.getMessage());
    }

    @Test
    void addNullSubTask() {
        manager.addSubTask(null);
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addEpic(epic1);
        final int taskId = manager.addSubTask(subTask1e1).getId();
        SubTask task1Upd = new SubTask(taskId, "Первая", "обновленная", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final SubTask updateTask = manager.updateSubTask(task1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(task1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongSubTask() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        SubTask task1Upd = new SubTask(3, "Первая", "обновленная", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        final SubTask updateTask = manager.updateSubTask(task1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getSubTaskByIdAsStandardBehavior() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addEpic(epic1);
        final int id = manager.addSubTask(subTask1e1).getId();
        final SubTask taskById = manager.getSubTaskById(id);

        assertEquals(subTask1e1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getSubTaskByWrongId() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        final SubTask taskById = manager.getSubTaskById(100);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteSubTaskByIdAndGetAllTasks() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1e1);
        int id = manager.addSubTask(subTask2e1).getId();
        manager.deleteSubTaskById(id);
        final List<SubTask> tasks = manager.getAllSubTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteSubTaskByWrongId() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        manager.addEpic(epic1);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicWithStandardBehaviorAndGetAllEpics() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        Epic epic2NoSubTask = new Epic("ВторойБезПодзадач", "");
        manager.addEpic(epic1);
        manager.addEpic(epic2NoSubTask);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(epic2NoSubTask, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNullEpic() {
        manager.addEpic(null);
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        final int taskId = manager.addEpic(epic1).getId();
        Epic epic1Upd = new Epic(taskId, "Первая", "обновленная");
        final Epic updateTask = manager.updateEpic(epic1Upd);

        assertNotNull(updateTask, "Задача не найдена.");
        assertEquals(epic1Upd, updateTask, "Задачи не совпадают.");
    }

    @Test
    void updateWrongEpic() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        manager.addEpic(epic1);
        Epic epic1Upd = new Epic(2, "Первая", "обновленная");
        final Epic updateTask = manager.updateEpic(epic1Upd);

        assertNull(updateTask, "Такой задачи не должно существовать.");
    }

    @Test
    void getEpicByIdAsStandardBehavior() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        final int id = manager.addEpic(epic1).getId();
        final Epic taskById = manager.getEpicById(id);

        assertEquals(epic1, taskById, "Задачи не совпадают.");
    }

    @Test
    void getEpicByWrongId() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        manager.addEpic(epic1);
        final Epic taskById = manager.getEpicById(3);

        assertNull(taskById, "Такой задачи не должно существовать.");
    }

    @Test
    void deleteEpicByIdAndGetAllEpics() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        Epic epic2NoSubTask = new Epic("ВторойБезПодзадач", "");

        manager.addEpic(epic1);
        int id = manager.addEpic(epic2NoSubTask).getId();
        manager.deleteEpicById(id);
        final List<Epic> tasks = manager.getAllEpics();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpicByWrongId() {
        Epic epic1 = new Epic("ПервыйПолный", "");
        Epic epic2NoSubTask = new Epic("ВторойБезПодзадач", "");
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        Epic epic2NoSubTask = new Epic("ВторойБезПодзадач", "");
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
        Epic epic1 = new Epic("ПервыйПолный", "");
        SubTask subTask1e1 = new SubTask("Первая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        SubTask subTask2e1 = new SubTask("Вторая", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
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

    @Test
    void getPrioritizedTasksWithNullTasks() {
        Task task1 = new Task("Первая", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Task task2 = new Task("Вторая", "описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        Task nullTimeTaskFirst = new Task("Без времени", "описание", TaskStatus.NEW, null, 0);
        Task nullTimeTaskLast = new Task("Без времени", "описание", TaskStatus.NEW, null, 0);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(nullTimeTaskFirst);
        manager.addTask(nullTimeTaskLast);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks, "История не возвращвется.");
        assertEquals(nullTimeTaskLast, prioritizedTasks.get(3), "Неверное количество задач.");


    }
}