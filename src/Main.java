import managers.Managers;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();

        System.out.println("Создали 11 задач "
                + manager.addTask(new Task("первая", " ", TaskStatus.NEW))
                + manager.addTask(new Task("2", " ", TaskStatus.NEW))
                + manager.addTask(new Task("3", " ", TaskStatus.NEW))
                + manager.addTask(new Task("4", " ", TaskStatus.NEW))
                + manager.addTask(new Task("5", " ", TaskStatus.NEW))
                + manager.addTask(new Task("6", " ", TaskStatus.NEW))
                + manager.addTask(new Task("7", " ", TaskStatus.NEW))
                + manager.addTask(new Task("8", " ", TaskStatus.NEW))
                + manager.addTask(new Task("9", " ", TaskStatus.NEW))
                + manager.addTask(new Task("10", " ", TaskStatus.NEW))
                + manager.addTask(new Task("последняя", " ", TaskStatus.NEW))
              );
        System.out.println("Просмотрели десять из них "
                        + manager.getTaskById(1)
                        + manager.getTaskById(2)
                        + manager.getTaskById(3)
                        + manager.getTaskById(4)
                        + manager.getTaskById(5)
                        + manager.getTaskById(6)
                        + manager.getTaskById(7)
                        + manager.getTaskById(8)
                        + manager.getTaskById(9)
                        + manager.getTaskById(10));

        System.out.println("Проверили историю просмотров " + manager.getHistory());

        System.out.println("Просмотрели еще задачу " + manager.getTaskById(11));

        System.out.println("Проверили историю просмотров " + manager.getHistory());


    }
}
