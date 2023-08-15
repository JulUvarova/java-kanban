import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultFileBackedTaskManager("saveFile.csv");

        System.out.println("Создаем задачи:"
                + manager.addTask(new Task("1", "nn", TaskStatus.NEW,
                LocalDateTime.of(2023, 11, 1, 1, 0),
                Duration.ofMinutes(100)))
                + manager.addEpic(new Epic("2", "nk"))
                + manager.addSubTask(new SubTask("3", "cdcd", TaskStatus.NEW, 2,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                Duration.ofMinutes(60)))
                + manager.addSubTask(new SubTask("4", "jb", TaskStatus.DONE, 2,
                LocalDateTime.of(2023, 2, 2, 0, 0),
                Duration.ofMinutes(120)))
        );
        manager.clearAllSubTasks();
        System.out.println(manager.getPrioritizedTasks());
    }
}
