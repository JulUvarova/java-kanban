package managers.taskManagers;

import exceptions.*;
import tasks.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String saveFile;
    private static final String SAVEFILE_HEADLINE = "id,type,name,status,description,startTime,duration,endTime,epic";

    public FileBackedTasksManager(String saveFile) {
        this.saveFile = saveFile;
    }

    public static FileBackedTasksManager loadFromFile(String readFile, String writeFile) {
        FileBackedTasksManager loadBTM = new FileBackedTasksManager(writeFile);
        try (FileReader fileReader = new FileReader(readFile)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) {
                    List<Integer> historyID = historyFromString(bufferedReader.readLine());
                    for (int id : historyID) {
                        if (loadBTM.tasks.containsKey(id)) {
                            loadBTM.historyManager.add(loadBTM.tasks.get(id));
                        } else if (loadBTM.subTasks.containsKey(id)) {
                            loadBTM.historyManager.add(loadBTM.subTasks.get(id));
                        } else if (loadBTM.epics.containsKey(id)) {
                            loadBTM.historyManager.add(loadBTM.epics.get(id));
                        }
                    }
                } else {
                    Task task = fromString(line);
                    int id = task.getId();
                    if (id > loadBTM.staticId) {
                        loadBTM.staticId = id;
                    }
                    switch (task.getTaskType()) {
                        case TASK:
                            loadBTM.tasks.put(task.getId(), task);
                            loadBTM.prioritizedTasks.add(task);
                            continue;
                        case EPIC:
                            loadBTM.epics.put(task.getId(), (Epic) task);
                            continue;
                        case SUBTASK:
                            SubTask subtask = (SubTask) task;
                            loadBTM.subTasks.put(id, subtask);
                            loadBTM.prioritizedTasks.add(task);
                            Epic epic = loadBTM.epics.get(subtask.getEpicId());
                            epic.addSubTaskId(id);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Такого файла не существует");
        }
        return loadBTM;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        if (task != null) {
            save();
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(Task task) {
        Task newTask = super.updateTask(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
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
    public SubTask getSubTaskById(int id) {
        SubTask newTask = super.getSubTaskById(id);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public SubTask addSubTask(SubTask task) {
        SubTask newTask = super.addSubTask(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        SubTask newTask = super.updateSubTask(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
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
    public Epic getEpicById(int id) {
        Epic newTask = super.getEpicById(id);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public Epic addEpic(Epic task) {
        Epic newTask = super.addEpic(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public Epic updateEpic(Epic task) {
        Epic newTask = super.updateEpic(task);
        if (newTask != null) {
            save();
            return newTask;
        } else {
            return null;
        }
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    protected void save() {
        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            fileWriter.write(SAVEFILE_HEADLINE);
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

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(task.getId()).append(",")
                .append(task.getTaskType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getStartTime()).append(",")
                .append(task.getDuration()).append(",")
                .append(task.getEndTime());
        if (task.getTaskType() == TaskType.SUBTASK) {
            SubTask subTask = (SubTask) task;
            sb.append(",").append(subTask.getEpicId());
        }
        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] line = value.split(",");
        Task taskFromString = null;

        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String name = line[2];
        TaskStatus taskStatus = TaskStatus.valueOf(line[3]);
        String description = line[4];
        LocalDateTime startTime = parseToLocalDateTime(line[5]);
        long duration = Long.parseLong(line[6]);

        switch (taskType) {
            case EPIC:
                LocalDateTime endTime = parseToLocalDateTime(line[7]);
                taskFromString = new Epic(id, name, description, taskStatus, startTime, duration, endTime);
                break;
            case TASK:
                taskFromString = new Task(id, name, description, taskStatus, startTime, duration);
                break;
            case SUBTASK:
                int epic = Integer.parseInt(line[8]);
                taskFromString = new SubTask(id, name, description, taskStatus, epic, startTime, duration);

                break;
        }

        return taskFromString;
    }

    private String historyToString() {
        return getHistory().stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        String[] line = value.split(",");
        List<Integer> historyFromString = new ArrayList<>();
        for (int i = 0; i < line.length; i++) {
            historyFromString.add(Integer.valueOf(line[i]));
        }
        return historyFromString;
    }

    private static LocalDateTime parseToLocalDateTime(String date) {
        if (!date.equals("null")) {
            return LocalDateTime.parse(date);
        }
        return null;
    }
}
