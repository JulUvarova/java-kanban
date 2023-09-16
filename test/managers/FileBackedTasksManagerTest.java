package managers;

import exceptions.ManagerSaveException;
import managers.taskManagers.FileBackedTasksManager;
import managers.taskManagers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    TaskManager loadManager;

    @BeforeEach
    void getManager() {
        manager = (FileBackedTasksManager) Managers.getDefaultFileBackedTaskManager("saveTestFile.csv");
        loadManager = Managers.getDefaultFileBackedTaskManager("backedTestFile.csv");
    }

    @Test
    void saveAndLoadWithEmptyFile() {
        try {
            Files.createFile(Path.of("saveTestFile.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertTrue(manager.getAllTasks().isEmpty(), "Список с задачами не пустой.");
        assertTrue(loadManager.getAllTasks().isEmpty(), "Список с задачами не пустой.");
        assertEquals(manager.getAllTasks(), loadManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), loadManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), loadManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), loadManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), loadManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), loadManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }

    @Test
    void saveAndLoadWithWrongFile() {
        ManagerSaveException exc = assertThrows(
                ManagerSaveException.class,
                () -> loadManager = FileBackedTasksManager
                        .loadFromFile("wrongSaveTestFile.csv", "backedTestFile.cvs"));

        assertEquals("Такого файла не существует", exc.getMessage());
    }

    @Test
    void saveAndLoadWithEmptyHistory() {
        Task task1 = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task1);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertEquals(manager.getAllTasks(), loadManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), loadManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), loadManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), loadManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), loadManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), loadManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }

    @Test
    void saveAndLoadAsStandardBehavior() {
        Task task1 = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Epic epic2 = new Epic("Эпик", "");
        SubTask subTask3e2 = new SubTask("Подзадача", "описание", TaskStatus.DONE, 2,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        int taskId = manager.addTask(task1).getId();
        int epicId = manager.addEpic(epic2).getId();
        int subTaskId = manager.addSubTask(subTask3e2).getId();
        manager.getTaskById(taskId);
        manager.getEpicById(epicId);
        manager.getSubTaskById(subTaskId);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertEquals(manager.getAllTasks(), loadManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), loadManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), loadManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), loadManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), loadManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), loadManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }

    @Test
    void saveAndLoadWithNoSubtaskEpic() {
        Epic epic2 = new Epic("Эпик", "");
        int epicId = manager.addEpic(epic2).getId();
        manager.getEpicById(epicId);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertEquals(manager.getAllTasks(), loadManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), loadManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), loadManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), loadManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), loadManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), loadManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }

    @AfterEach
    void deleteFile() {
        try {
            Files.deleteIfExists(Path.of("saveTestFile.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}