package managers;

import com.google.gson.Gson;
import exceptions.RequestException;
import managers.taskManagers.HttpTaskManager;
import org.junit.jupiter.api.*;
import server.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer server;
    HttpTaskManager actualManager;

    @BeforeEach
    void start() throws IOException {
        server = new KVServer();
        server.start();
        manager = (HttpTaskManager) Managers.getDefaultHttpTaskManager("http://localhost:8078");
        actualManager = new HttpTaskManager("http://localhost:8078");
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Test
    void saveAndLoadWithEmptyData() {
        HttpTaskManager actualManager = new HttpTaskManager("http://localhost:8078");
        actualManager.load();

        assertTrue(manager.getAllTasks().isEmpty(), "Список с задачами не пустой.");
        assertTrue(actualManager.getAllTasks().isEmpty(), "Список с задачами не пустой.");
        assertEquals(manager.getAllTasks(), actualManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), actualManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), actualManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), actualManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), actualManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), actualManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }


    @Test
    void saveAndLoadWithWrongFile() {
        RequestException exc = assertThrows(
                RequestException.class,
                () -> actualManager = new HttpTaskManager("http://localhost:8000"));

        assertEquals("Невозможно выполнить запрос: Connection refused: no further information", exc.getMessage());
    }

    @Test
    void saveAndLoadWithEmptyHistory() {
        Task task1 = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task1);

        HttpTaskManager actualManager = new HttpTaskManager("http://localhost:8078");
        actualManager.load();

        assertEquals(manager.getAllTasks(), actualManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), actualManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), actualManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), actualManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), actualManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), actualManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }

    @Test
    void saveAndLoadAsStandardBehavior() {
        Task task1 = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        Epic epic2 = new Epic("Эпик", "");
        SubTask subTask3e2 = new SubTask("Подзадача", "описание", TaskStatus.DONE, 2,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);

        manager.addTask(task1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask3e2);

        manager.getEpicById(2);
        manager.getSubTaskById(3);
        manager.getTaskById(1);

        HttpTaskManager actualManager = new HttpTaskManager("http://localhost:8078");
        actualManager.load();

        assertEquals(manager.getAllTasks(), actualManager.getAllTasks(),
                "Список задач после выгрузки не совпадает.");
        assertEquals(manager.getAllSubTasks(), actualManager.getAllSubTasks(),
                "Список подзадач после выгрузки не совпадает.");
        assertEquals(manager.getAllEpics(), actualManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает.");
        assertEquals(manager.getHistory(), actualManager.getHistory(),
                "История после выгрузки не совпадает.");
        assertEquals(manager.getPrioritizedTasks(), actualManager.getPrioritizedTasks(),
                "Упорядоченный список после выгрузки не совпадает.");
        assertEquals(manager.getLastStaticId(), actualManager.getLastStaticId(),
                "Счетчик задач после выгрузки не совпадает.");
    }
}