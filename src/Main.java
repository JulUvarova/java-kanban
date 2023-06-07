import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        // сценарий с обычными задачами:
        Task task1 = new Task("помыть посуду", " ", TaskStatus.NEW);
        System.out.println("Создали задачу " + manager.addTask(task1));
        Task task2 = new Task("помыть кота", " ", TaskStatus.NEW);
        System.out.println("Создали задачу " + manager.addTask(task2));
        System.out.println("Вывели список задач " + manager.getAllTasks());
        System.out.println("Обновили первую задачу " + manager.updateTask(1,"помыть посуду", " ", TaskStatus.DONE));
        System.out.println("Очистили список задач " + manager.clearAllTasks());

        // сценарий с эпик-2 подзадачи и эпик-0
        Epic epic1 = new Epic("Заварить чай", " ", new ArrayList<>());
        System.out.println("Создали новый эпик " + manager.addEpic(epic1));
        System.out.println("Узнали ID эпиков " + manager.getAllEpics());
        SubTask subTask1 = new SubTask("Насыпать чай", " ", TaskStatus.NEW);
        System.out.println("Создали подзадачу в эпик" + manager.addSubTask(subTask1, 3));
        SubTask subTask2 = new SubTask("Добавить кипяток", " ", TaskStatus.NEW);
        System.out.println("Создали подзадачу в эпик" + manager.addSubTask(subTask2, 3));
        Epic epic2 = new Epic("Погулять с собакой", " ", new ArrayList<>());
        System.out.println("Создали новый эпик " + manager.addEpic(epic2));
        System.out.println("Посмотрели список эпиков " + manager.getAllEpics());
        System.out.println("Посмотрели эпик по ID " + manager.getEpicById(3));
        System.out.println("Посмотрели список подзадач эпика " + manager.getSubTaskByEpic(3));
        System.out.println("Поменяли статус подзадачи 1 " + manager.updateSubTask(4, "Насыпать чай", " ", TaskStatus.DONE));
        System.out.println("Обновили эпик " + manager.updateEpic(3, "Заварить чай", " "));
        System.out.println("Поменяли статус подзадачи 2 " + manager.updateSubTask(5, "Добавить кипяток", " ", TaskStatus.DONE));
        System.out.println("Обновили эпик " + manager.updateEpic(3, "Заварить чай", " "));
        System.out.println("Удалили одну подзадачу из эпика " + manager.deleteSubTaskById(4));
        System.out.println("Обновили эпик " + manager.updateEpic(3, "Заварить чай", " "));
        System.out.println("Очистили подзадачи " + manager.clearAllSubTasks());
        System.out.println("Обновили эпик " + manager.updateEpic(3, "Заварить чай", " "));
        System.out.println("Удалили второй эпик " + manager.deleteEpicById(6));
        System.out.println("Посмотрели список эпиков " + manager.getAllEpics());
        System.out.println("Очистили список эпиков " + manager.clearAllEpics());
        System.out.println("Посмотрели список эпиков " + manager.getAllEpics());
    }
}
