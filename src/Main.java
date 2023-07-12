import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();

        System.out.println("Создали задачу: " + manager.addTask(
                new Task("(1)", "...", TaskStatus.NEW)));
        System.out.println("Создали задачу: " + manager.addTask(
                new Task("2", "...", TaskStatus.NEW)));
        System.out.println("Создали новый эпик: " + manager.addEpic(
                new Epic("(3)", "...")));
        System.out.println("Создали подзадачу в эпик: " + manager.addSubTask(
                new SubTask("(4/3)", "...", TaskStatus.NEW), 3));
        System.out.println("Создали подзадачу в эпик: " + manager.addSubTask(
                new SubTask("(5/3)", "...", TaskStatus.NEW), 3));
        System.out.println("Создали подзадачу в эпик: " + manager.addSubTask(
                new SubTask("(6/3)", "...", TaskStatus.NEW), 3));
        System.out.println("Создали новый эпик без подзадач: " + manager.addEpic(
                new Epic("(7)", "...")));

        System.out.println("Запросили эпик 7: " + manager.getEpicById(7));
        System.out.println("Запросили подзадачу 5: " + manager.getSubTaskById(5));
        System.out.println("Запросили эпик 3: " + manager.getEpicById(3));
        System.out.println("Запросили задачу 1: " + manager.getTaskById(1));
        System.out.println("Проверили историю: " + manager.getHistory());

        System.out.println("Запросили задачу 1: " + manager.getTaskById(1));
        System.out.println("Запросили эпик 3: " + manager.getEpicById(3));
        System.out.println("Запросили подзадачу 5: " + manager.getSubTaskById(5));
        System.out.println("Запросили эпик 7: " + manager.getEpicById(7));
        System.out.println("Проверили историю: " + manager.getHistory());

        System.out.println("Удалили задачу 1: " + manager.deleteTaskById(1));
        System.out.println("Проверили историю: " + manager.getHistory());

        System.out.println("Удалили эпик 3: " + manager.deleteEpicById(3));
        System.out.println("Проверили историю: " + manager.getHistory());
    }
}
