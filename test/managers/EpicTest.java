package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static TaskManager manager;
    static Epic epic1;
    static SubTask subTaskNew;
    static SubTask subTaskNew2;
    static SubTask subTaskDone;
    static SubTask subTaskDone2;
    static SubTask subTaskInProgress;
    static SubTask subTaskInProgress2;

    @BeforeEach
    void getManager() {
        manager = Managers.getDefaultTaskManager();
        epic1 = new Epic("ПервыйПолный", "");
        subTaskNew = new SubTask("Новая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        subTaskNew2 = new SubTask("Новая", "описание", TaskStatus.NEW, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        subTaskInProgress = new SubTask("В процессе", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 1, 12, 0), 60);
        subTaskInProgress2 = new SubTask("В процессе", "описание", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 9, 2, 12, 0), 60);
        subTaskDone = new SubTask("Завершенная", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 7, 1, 1, 0), 60);
        subTaskDone2 = new SubTask("Завершенная", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 7, 1, 3, 0), 60);
    }

    @Test
    void epicStatusWithoutSubtask() {
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.NEW, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenAllSubtaskNew() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskNew);
        manager.addSubTask(subTaskNew2);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.NEW, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenAllSubtaskDone() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskDone);
        manager.addSubTask(subTaskDone2);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.DONE, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenAllSubtaskInProgress() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskInProgress);
        manager.addSubTask(subTaskInProgress2);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenSubtaskNewAndDone() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskDone);
        manager.addSubTask(subTaskNew);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenSubtaskNewAndInProgress() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskNew);
        manager.addSubTask(subTaskInProgress);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус.");
    }

    @Test
    void epicStatusWhenSubtaskInProgressAndDone() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskDone);
        manager.addSubTask(subTaskInProgress);
        TaskStatus epicStatus = manager.addEpic(epic1).getStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус.");
    }

    @Test
    void estimateEpicTimeValuesWithSingleSubtask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskNew);
        Epic epic = manager.getEpicById(1);
        LocalDateTime startTime = epic.getStartTime();
        LocalDateTime endTime = epic.getEndTime();
        long duration = epic.getDuration();

        assertEquals(subTaskNew.getStartTime(), startTime, "Неверное время начала.");
        assertEquals(subTaskNew.getEndTime(), endTime, "Неверное время завершения.");
        assertEquals(subTaskNew.getDuration(), duration, "Неверная продолжительность.");
    }

    @Test
    void estimateEpicTimeValuesWithSomeSubtask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskNew);
        manager.addSubTask(subTaskDone);
        Epic epic = manager.getEpicById(1);
        LocalDateTime startTime = epic.getStartTime();
        LocalDateTime endTime = epic.getEndTime();
        long duration = epic.getDuration();

        assertEquals(subTaskDone.getStartTime(), startTime, "Неверное время начала.");
        assertEquals(subTaskNew.getEndTime(), endTime, "Неверное время завершения.");
        assertEquals(subTaskNew.getDuration() + subTaskDone.getDuration()
                , duration, "Неверная продолжительность.");
    }

    @Test
    void getSubtaskByEpicId() {
        manager.addEpic(epic1);
        manager.addSubTask(subTaskNew);
        manager.addSubTask(subTaskDone);
        List<SubTask> subTaskByEpic = manager.getSubTaskByEpic(1);

        assertEquals(2, subTaskByEpic.size(), "Неверное число подзадач.");
    }
}