import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultNewTaskManager("saveFile.csv");

        System.out.println("Создаем задачи:");
        manager.addTask(new Task("1", "nn", TaskStatus.NEW));
        manager.addEpic(new Epic("2", "nk"));
        manager.addSubTask(new SubTask("3","cdcd", TaskStatus.NEW), 2);

        System.out.println("Выводим задачи:");
        manager.getEpicById(2);
        manager.getSubTaskById(3);

        System.out.println("Смотрим историю:");
        System.out.println(manager.getHistory());


    }
}
