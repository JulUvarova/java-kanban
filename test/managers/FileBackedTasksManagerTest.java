package managers;

import exceptions.ManagerSaveException;
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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {
    TaskManager saveManager;
    TaskManager loadManager;
    Task task1;
    Epic epic2;
    SubTask subTask3e2;

    @BeforeEach
    void getManager() {
        saveManager = Managers.getDefaultFileBackedTaskManager("saveTestFile.csv");
        loadManager = Managers.getDefaultFileBackedTaskManager("backedTestFile.csv");
        task1 = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0),
                Duration.ofMinutes(120));
        epic2 = new Epic("Эпик", "");
        subTask3e2 = new SubTask("Подзадача", "описание", TaskStatus.DONE, 2,
                LocalDateTime.of(2023, 8, 14, 19, 0),
                Duration.ofMinutes(120));
    }

    @Test
    void saveAndLoadWithEmptyFile() {
        int id = saveManager.addTask(task1).getId();
        saveManager.deleteTaskById(id);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertTrue(saveManager.getAllTasks().isEmpty());
        assertTrue(loadManager.getAllTasks().isEmpty());
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
        saveManager.addTask(task1);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");
        Task taskSave = saveManager.getAllTasks().get(0);
        Task taskLoad = loadManager.getAllTasks().get(0);

        assertEquals(taskSave, taskLoad, "Задачи не совпадают.");
    }

    @Test
    void saveAndLoadAsStandardBehavior() {
        int taskId = saveManager.addTask(task1).getId();
        int epicId = saveManager.addEpic(epic2).getId();
        int subTaskId = saveManager.addSubTask(subTask3e2).getId();
        saveManager.getTaskById(taskId);
        saveManager.getEpicById(epicId);
        saveManager.getSubTaskById(subTaskId);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertEquals(saveManager.getAllTasks().get(0), loadManager.getAllTasks().get(0), "Задачи не совпадают.");
        assertEquals(saveManager.getAllEpics().get(0), loadManager.getAllEpics().get(0), "Эпики не совпадают.");
        assertEquals(saveManager.getAllSubTasks().get(0), loadManager.getAllSubTasks().get(0),
                "Подзадачи не совпадают.");
        assertEquals(saveManager.getHistory(), loadManager.getHistory(), "Истории не совпадают.");
    }

    @Test
    void saveAndLoadWithNoSubtaskEpic() {
        int epicId = saveManager.addEpic(epic2).getId();
        saveManager.getEpicById(epicId);
        loadManager = FileBackedTasksManager.loadFromFile("saveTestFile.csv", "backedTestFile.cvs");

        assertEquals(saveManager.getAllEpics().get(0), loadManager.getAllEpics().get(0), "Эпики не совпадают.");
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