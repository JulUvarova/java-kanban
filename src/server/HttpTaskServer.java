package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exceptions.TimeValidateException;
import managers.Managers;
import managers.TaskManager;
import tasks.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    private static final TaskManager manager = Managers.getDefaultFileBackedTaskManager("save_8");
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();

    public HttpTaskServer() throws IOException {
//        Task task1 = new Task("Задача", "описание", TaskStatus.NEW,
//                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
//        Epic epic1 = new Epic("ПервыйПолный", "");
//        SubTask subTaskNew = new SubTask("Новая", "описание", TaskStatus.NEW, 2,
//                LocalDateTime.of(2023, 9, 13, 19, 0), 120);
//        manager.addTask(task1);
//        manager.addEpic(epic1);
//        manager.addSubTask(subTaskNew);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());

        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String requestPath[] = (exchange.getRequestURI().getPath().split("/"));
            Optional<String> requestQuery = Optional.ofNullable(exchange.getRequestURI().getQuery());
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

            Endpoint endpoint = getEndpoint(method, requestPath, requestQuery);

            switch (endpoint) {
                case GetTasks:
                    List<Task> tasks = manager.getAllTasks();
                    if (tasks.size() == 0) {
                        writeResponse(exchange, "Задач пока не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(tasks), 200);
                    }
                    break;
                case GetTaskById:
                    Task task = manager.getTaskById(getId(requestQuery.get()));
                    if (task == null) {
                        writeResponse(exchange, "Задачи с " + requestQuery.get() + " не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(task), 200);
                    }
                    break;
                case PostTask:
                    Task taskToPost = gson.fromJson(body, Task.class);
                    List<Task> tasksInMemory = manager.getAllTasks();
                    for (Task t : tasksInMemory) {
                        if (t.getId() == taskToPost.getId()) {
                            try {
                                manager.updateTask(taskToPost);
                                writeResponse(exchange, "Задачa с id:" + taskToPost.getId() + " обновлена", 200);
                            } catch (TimeValidateException e) {
                                writeResponse(exchange, "Это время занято другой задачей", 400);
                            }
                            return;
                        }
                    }
                    try {
                        manager.addTask(taskToPost);
                        writeResponse(exchange, "Задачa добавлена", 200);
                    } catch (TimeValidateException e) {
                        writeResponse(exchange, "Это время занято другой задачей", 400);
                    }
                    break;
                case DeleteTasks:
                    manager.clearAllTasks();
                    writeResponse(exchange, "Все задачи удалены", 200);
                    break;
                case DeleteTaskById:
                    Task taskToDelete = manager.getTaskById(getId(requestQuery.get()));
                    if (taskToDelete == null) {
                        writeResponse(exchange, "Задачи с " + requestQuery.get() + " не существует", 404);
                    } else {
                        manager.deleteTaskById(getId(requestQuery.get()));
                        writeResponse(exchange, "Задача с " + requestQuery.get() + " удалена", 200);
                    }
                    break;
                case GetSubTasks:
                    List<SubTask> subtasks = manager.getAllSubTasks();
                    if (subtasks.size() == 0) {
                        writeResponse(exchange, "Подзадач пока не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(subtasks), 200);
                    }
                    break;
                case GetSubTaskById:
                    SubTask subtask = manager.getSubTaskById(getId(requestQuery.get()));
                    if (subtask == null) {
                        writeResponse(exchange, "Подзадачи с " + requestQuery.get() + " не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(subtask), 200);
                    }
                    break;
                case PostSubTask:
                    SubTask subtaskToPost = gson.fromJson(body, SubTask.class);
                    List<SubTask> subtasksInMemory = manager.getAllSubTasks();
                    for (SubTask t : subtasksInMemory) {
                        if (t.getId() == subtaskToPost.getId()) {
                            try {
                                manager.updateSubTask(subtaskToPost);
                                writeResponse(exchange, "Подзадачa с id:" + subtaskToPost.getId() + " обновлена", 200);
                            } catch (TimeValidateException e) {
                                writeResponse(exchange, "Это время занято другой задачей", 400);
                            }
                            return;
                        }
                    }
                    try {
                        manager.addSubTask(subtaskToPost);
                        writeResponse(exchange, "Подзадачa добавлена", 200);
                    } catch (TimeValidateException e) {
                        writeResponse(exchange, "Это время занято другой задачей", 400);
                    }
                    break;
                case DeleteSubTasks:
                    manager.clearAllSubTasks();
                    writeResponse(exchange, "Все подзадачи удалены", 200);
                    break;
                case DeleteSubTaskById:
                    SubTask subtaskToDelete = manager.getSubTaskById(getId(requestQuery.get()));
                    if (subtaskToDelete == null) {
                        writeResponse(exchange, "Подзадачи с " + requestQuery.get() + " не существует", 404);
                    } else {
                        manager.deleteSubTaskById(getId(requestQuery.get()));
                        writeResponse(exchange, "Подзадача c " + requestQuery.get() + " удалена", 200);
                    }
                    break;
                case GetEpics:
                    List<Epic> epics = manager.getAllEpics();
                    if (epics.size() == 0) {
                        writeResponse(exchange, "Эпиков пока не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(epics), 200);
                    }
                    break;
                case GetEpicById:
                    Epic epic = manager.getEpicById(getId(requestQuery.get()));
                    if (epic == null) {
                        writeResponse(exchange, "Эпик с " + requestQuery.get() + " не существует", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(epic), 200);
                    }
                    break;
                case PostEpic:
                    Epic epicToPost = gson.fromJson(body, Epic.class);
                    List<Epic> epicInMemory = manager.getAllEpics();
                    for (Epic t : epicInMemory) {
                        if (t.getId() == epicToPost.getId()) {
                            manager.updateEpic(epicToPost);
                            writeResponse(exchange, "Эпик с id:" + epicToPost.getId() + " обновлен", 200);
                            return;
                        }
                    }
                    manager.addEpic(epicToPost);
                    writeResponse(exchange, "Эпик добавлен", 200);
                    break;
                case DeleteEpics:
                    manager.clearAllEpics();
                    writeResponse(exchange, "Все эпики удалены", 200);
                    break;
                case DeleteEpicById:
                    Epic epicToDelete = manager.getEpicById(getId(requestQuery.get()));
                    if (epicToDelete == null) {
                        writeResponse(exchange, "Эпик с " + requestQuery.get() + " не существует", 404);
                    } else {
                        manager.deleteEpicById(getId(requestQuery.get()));
                        writeResponse(exchange, "Эпик с id:" + requestQuery.get() + " удален", 200);
                    }
                    break;
                case GetHistory:
                    List<Task> history = manager.getHistory();
                    if (history.size() == 0) {
                        writeResponse(exchange, "История просмотров пустая", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(history), 200);
                    }
                    break;
                case getPrioritizedTasks:
                    List<Task> listTasks = manager.getPrioritizedTasks();
                    if (listTasks.size() == 0) {
                        writeResponse(exchange, "Список задач пуст", 404);
                    } else {
                        writeResponse(exchange, gson.toJson(listTasks), 200);
                    }
                    break;
                case GetSubByEpic:
                    Epic epicWithSub = manager.getEpicById(getId(requestQuery.get()));
                    if (epicWithSub == null) {
                        writeResponse(exchange, "Эпик с " + requestQuery.get() + " не существует", 404);
                    } else {
                        List<SubTask> subTaskByEpic = manager.getSubTaskByEpic(getId(requestQuery.get()));
                        writeResponse(exchange, gson.toJson(subTaskByEpic), 200);
                    }
                    break;
                case Unknown:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                    break;
            }
        }
    }

    private Endpoint getEndpoint(String method, String[] path, Optional<String> query) {
        switch (method) {
            case "GET":
                if ((path.length == 2) && query.isEmpty()) {
                    return Endpoint.getPrioritizedTasks;
                }
                if (path[2].equals("task") && query.isEmpty()) {
                    return Endpoint.GetTasks;
                }
                if (path[2].equals("task") && query.isPresent()) {
                    return Endpoint.GetTaskById;
                }
                if (path[2].equals("subtask") && (path.length == 3) && query.isEmpty()) {
                    return Endpoint.GetSubTasks;
                }
                if (path[2].equals("subtask") && (path.length == 3) && query.isPresent()) {
                    return Endpoint.GetSubTaskById;
                }
                if (path[2].equals("subtask") && path[3].equals("epic") && query.isPresent()) {
                    return Endpoint.GetSubByEpic;
                }
                if (path[2].equals("epic") && query.isEmpty()) {
                    return Endpoint.GetEpics;
                }
                if (path[2].equals("epic") && query.isPresent()) {
                    return Endpoint.GetEpicById;
                }
                if (path[2].equals("history") && query.isEmpty()) {
                    return Endpoint.GetHistory;
                }
            case "POST":
                if (path[2].equals("task")) {
                    return Endpoint.PostTask;
                }
                if (path[2].equals("subtask")) {
                    return Endpoint.PostSubTask;
                }
                if (path[2].equals("epic")) {
                    return Endpoint.PostEpic;
                }
            case "DELETE":
                if (path[2].equals("task") && query.isEmpty()) {
                    return Endpoint.DeleteTasks;
                }
                if (path[2].equals("task") && query.isPresent()) {
                    return Endpoint.DeleteTaskById;
                }
                if (path[2].equals("subtask") && query.isEmpty()) {
                    return Endpoint.DeleteSubTasks;
                }
                if (path[2].equals("subtask") && query.isPresent()) {
                    return Endpoint.DeleteSubTaskById;
                }
                if (path[2].equals("epic") && query.isEmpty()) {
                    return Endpoint.DeleteEpics;
                }
                if (path[2].equals("epic") && query.isPresent()) {
                    return Endpoint.DeleteEpicById;
                }
            default:
                return Endpoint.Unknown;
        }
    }

    private void writeResponse(HttpExchange exchange,
                                      String responseString,
                                      int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private int getId(String str) {
        StringBuilder sb = new StringBuilder(str);
        String id = str.substring(3).toString();
        return Integer.parseInt(id);
    }
}