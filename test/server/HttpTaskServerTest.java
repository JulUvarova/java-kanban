package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.taskManagers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpTaskServer server;
    Gson gson = Managers.getGson();
    TaskManager manager;
    URI taskUri = URI.create("http://localhost:8080/tasks/task/");
    URI subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
    URI epicUri = URI.create("http://localhost:8080/tasks/epic/");
    URI historyUri = URI.create("http://localhost:8080/tasks/history/");
    URI prioriUri = URI.create("http://localhost:8080/tasks/");
    String id = "?id=";

    @BeforeEach
    void start() throws IOException {
        manager = Managers.getDefaultTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Test
    void postEmptyTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, response.statusCode(), "Ответ сервера некорректный");
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        task.setId(manager.getLastStaticId()); // иначе она id = 0
        Task actualTask = manager.getTaskById(1);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(task, actualTask, "Задачи не совпадают");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);

        manager.addTask(task); // id = 1
        Task taskUp = new Task(manager.getLastStaticId(), "Обновленная задача", "описание", TaskStatus.DONE,
                LocalDateTime.of(2023, 9, 13, 19, 0), 120);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(taskUp);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Task actualTask = manager.getTaskById(1);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(taskUp, actualTask, "Задачи не совпадают");
    }

    @Test
    void postEmptySubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(subtaskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, response.statusCode(), "Ответ сервера некорректный");
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(subTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(subtaskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        subTask.setId(manager.getLastStaticId()); // иначе она id = 0
        SubTask actualTask = manager.getSubTaskById(2);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(subTask, actualTask, "Задачи не совпадают");
    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask); // id = 2

        SubTask taskUp = new SubTask(2, "Обновленная подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(taskUp);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(subtaskUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        SubTask actualTask = manager.getSubTaskById(2);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(taskUp, actualTask, "Задачи не совпадают");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        epic.setId(manager.getLastStaticId());
        Epic actualTask = manager.getEpicById(1);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(epic, actualTask, "Задачи не совпадают");
    }

    @Test
    void postEmptyEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, response.statusCode(), "Ответ сервера некорректный");
    }
    @Test
    void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        Epic epicUp = new Epic(1, "Эпик обновленный", "");

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epicUp);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .POST(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Epic actualTask = manager.getEpicById(1);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(epicUp, actualTask, "Задачи не совпадают");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskUri + id + "1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(task, actualTask, "Задачи не совпадают");
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "Задача не возвращается");
        assertEquals(manager.getAllTasks().size(), actualTask.size(), "Количесво задач не совпадает");
        assertEquals(task, actualTask.get(0), "Задачи не совпадают");
    }

    @Test
    void getSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(subtaskUri + id + "2"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        SubTask actualTask = gson.fromJson(response.body(), SubTask.class);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(subTask, actualTask, "Задачи не совпадают");
    }

    @Test
    void getAllSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(subtaskUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "Задача не возвращается");
        assertEquals(manager.getAllSubTasks().size(), actualTask.size(), "Количесво задач не совпадает");
        assertEquals(subTask, actualTask.get(0), "Задачи не совпадают");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(epicUri + id + "1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Epic actualTask = gson.fromJson(response.body(), Epic.class);

        assertNotNull(actualTask, "Задача не возвращается");
        assertEquals(epic, actualTask, "Задачи не совпадают");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<SubTask> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "Задача не возвращается");
        assertEquals(manager.getAllEpics().size(), actualTask.size(), "Количесво задач не совпадает");
        assertEquals(epic, actualTask.get(0), "Задачи не совпадают");
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskUri + id + "1"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Task taskDel = manager.getTaskById(1);
        assertNull(taskDel, "Задача не удалена");
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        assertEquals(0, manager.getAllTasks().size(), "Количесво задач не совпадает");
    }

    @Test
    void deleteSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(subtaskUri + id + "2"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        SubTask taskDel = manager.getSubTaskById(1);
        assertNull(taskDel, "Задача не удалена");
    }

    @Test
    void deleteAllSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(subtaskUri)
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        assertEquals(0, manager.getAllSubTasks().size(), "Количесво задач не совпадает");
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(epicUri + id + "1"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Epic taskDel = manager.getEpicById(1);
        assertNull(taskDel, "Задача не удалена");
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        assertEquals(0, manager.getAllEpics().size(), "Количесво задач не совпадает");
    }

    @Test
    void getSubTaskByEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "");
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, 1,
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(subtaskUri + "epic" + id + "1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "Задача не возвращается");
        assertEquals(manager.getSubTaskByEpic(1).size(), actualTask.size(), "Количество задач не совпадает");
        assertEquals(subTask, actualTask.get(0), "Задачи не совпадают");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);

        manager.getTaskById(1);
        manager.getEpicById(2);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(historyUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "История не возвращается");
        assertEquals(manager.getHistory().size(), actualTask.size(), "История не совпадает");
        assertEquals(task, actualTask.get(0), "Элементы истории не совпадают");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача", "описание", TaskStatus.NEW,
                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
        manager.addTask(task);
        Epic epic = new Epic("Эпик", "");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "описание", TaskStatus.DONE, epic.getId(),
                LocalDateTime.of(2023, 8, 14, 19, 0), 120);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(prioriUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера некорректный");

        Type typeTask = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actualTask = gson.fromJson(response.body(), typeTask);

        assertNotNull(actualTask.get(0), "Задачи не возвращается");
        assertEquals(manager.getPrioritizedTasks().size(), actualTask.size(), "Список задач не совпадает");
    }

    @Test
    void getWrongRequest() throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("body");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .PUT(body)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode(), "Ответ сервера некорректный");
    }
}