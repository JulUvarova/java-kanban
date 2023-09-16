package managers;

import managers.hisroryManager.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    void start() {
        historyManager = Managers.getDefaultHistoryManager();
        task1 = new Task(1, "Первая", "", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        task2 = new Task(2, "Вторая", "", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        task3 = new Task(3, "Третья", "", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 10, 1, 12, 0),60);
    }

    @Test
    void addTaskInEmptyHistory() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void addDuplicatedTask() {
        historyManager.add(task1);
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Задача продублировалась в истории");
    }

    @Test
    void addTaskInNoEmptyHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "В историю не добавилась вторая задача.");
    }

    @Test
    void getHistoryWithEmptyMap() {
        historyManager.getHistory();
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void getHistoryWithNoEmptyMap() {
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История неверна.");
    }

    @Test
    void removeFirstTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалилась.");
    }

    @Test
    void removeLastTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалилась.");
    }

    @Test
    void removeAnyTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалилась.");
    }

    @Test
    void removeSingleTaskInHistory() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(0, history.size(), "Задача не удалилась.");
    }

    @Test
    void removeTaskWithWrongIdInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task3.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История неверна.");
    }
}