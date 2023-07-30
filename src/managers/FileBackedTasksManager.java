package managers;

import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String saveFile;

    public FileBackedTasksManager(String saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        System.out.println("Восстанавливаем менеджер из памяти.");
        FileBackedTasksManager fbtm = loadFromFile("saveFile.csv", "saveFile2");
        System.out.println("Проверяем, что восстановилось из файла:");
        fbtm.getAllEpics();
        fbtm.getAllSubTasks();
        fbtm.getAllTasks();
        System.out.println("Проверяем историю просмотров:" + fbtm.getHistory());
//        System.out.println("Проверяем не сбилось ли ID");
//        fbtm.addTask(new Task("4", "nkk", TaskStatus.NEW));
//        System.out.println("Просматриваем созданную задачу");
//        fbtm.getTaskById(4);
//        System.out.println("Проверяем историю просмотров:" + fbtm.getHistory());
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            fileWriter.write("id,type,name,status,description,epic");
            for (Task task : tasks.values()) {
                String line = toString(task);
                fileWriter.write(line);
            }
            for (Task task : epics.values()) {
                String line = toString(task);
                fileWriter.write(line);
            }
            for (Task task : subTasks.values()) {
                String line = toString(task);
                fileWriter.write(line);
            }
            fileWriter.write("\n\n" + historyToString());
        } catch (IOException e) {
            throw new ManagerSaveException("Такого файла не существует");
        }
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(task.getID()).append(",").append(task.getTaskType()).append(",").append(task.getName())
                .append(",").append(task.getStatus()).append(",").append(task.getDescription());
        if (task.getTaskType() == TaskType.SUBTASK) {
            SubTask subTask = (SubTask) task;
            sb.append(",").append(subTask.getEpicId());
        }
        String taskToString = sb.toString();
        return taskToString;
    }

    public static Task fromString(String value) {
        String[] line = value.split(",");
        Task taskFromString = null;

        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String name = line[2];
        TaskStatus taskStatus = TaskStatus.valueOf(line[3]);
        String description = line[4];

        switch (taskType) {
            case EPIC:
                taskFromString = new Epic(id, name, description, new HashSet<>(), taskStatus);
                break;
            case TASK:
                taskFromString = new Task(id, name, description, taskStatus);
                break;
            case SUBTASK:
                int epic = Integer.parseInt(line[5]);
                taskFromString = new SubTask(id, name, description, taskStatus, epic);
                taskFromString.setID(id);
                break;
        }

        return taskFromString;
    }

    public static String historyToString() {
        List<Task> history = historyManager.getHistory();
        String[] taskNumberHistory = new String[history.size()];
        int count = 0;
        for (Task task : history) {
            taskNumberHistory[count] = String.valueOf(task.getID());
            count++;
        }
        String historyToString = String.join(",", taskNumberHistory);
        return historyToString;
    }

    public static List<Integer> historyFromString(String value) {
        String[] line = value.split(",");
        List<Integer> historyFromString = new ArrayList<>();
        for (int i = 0; i < line.length; i++) {
            historyFromString.add(Integer.valueOf(line[i]));
        }
        return historyFromString;
    }

    public static FileBackedTasksManager loadFromFile(String readFile, String writeFile) {
        FileBackedTasksManager loadBTM = new FileBackedTasksManager(writeFile);
        try (FileReader fileReader = new FileReader(readFile)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.contains("id,type,name")) {
                    continue;
                } else if (line.isBlank()) {
                    List<Integer> historyID = historyFromString(bufferedReader.readLine());
                    for (int id : historyID) {
                        if (loadBTM.tasks.containsKey(id)) {
                            loadBTM.historyManager.add(id, tasks.get(id));
                        } else if (loadBTM.subTasks.containsKey(id)) {
                            loadBTM.historyManager.add(id, subTasks.get(id));
                        } else if (loadBTM.epics.containsKey(id)) {
                            loadBTM.historyManager.add(id, epics.get(id));
                        }
                    }
                } else {
                    Task task = fromString(line);
                    switch (task.getTaskType()) {
                        case TASK:
                            loadBTM.tasks.put(task.getID(), task);
                            continue;
                        case EPIC:
                            loadBTM.epics.put(task.getID(), (Epic) task);
                            continue;
                        case SUBTASK:
                            loadBTM.subTasks.put(task.getID(), (SubTask) task);
                    }
                }
            }
            int max = 0;
            for (int i : loadBTM.tasks.keySet()) {
                if (i > max) {
                    max = i;
                }
            }
            for (int i : loadBTM.epics.keySet()) {
                if (i > max) {
                    max = i;
                }
            }
            for (int i : loadBTM.subTasks.keySet()) {
                if (i > max) {
                    max = i;
                }
            }
            loadBTM.setStaticID(max);

        } catch (IOException e) {
            throw new ManagerSaveException("Такого файла не существует");
        }
        return loadBTM;
    }

    public void setStaticID(int newStaticID) {
        staticId = newStaticID;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void getTaskById(int id) {
        super.getTaskById(id);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(int id, String name, String description, TaskStatus status) {
        super.updateTask(id, name, description, status);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void clearAllSubTasks() {
        super.clearAllSubTasks();
        save();
    }

    @Override
    public void getSubTaskById(int id) {
        super.getSubTaskById(id);
        save();
    }

    @Override
    public void addSubTask(SubTask task, int id) {
        super.addSubTask(task, id);
        save();
    }

    @Override
    public void updateSubTask(int id, String name, String description, TaskStatus status) {
        super.updateSubTask(id, name, description, status);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void getEpicById(int id) {
        super.getEpicById(id);
        save();
    }

    @Override
    public void addEpic(Epic task) {
        super.addEpic(task);
        save();
    }

    @Override
    public void updateEpic(int id, String name, String description) {
        super.updateEpic(id, name, description);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }
}
