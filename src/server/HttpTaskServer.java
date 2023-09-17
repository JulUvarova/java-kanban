package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.TimeValidateException;
import managers.Managers;
import managers.taskManagers.TaskManager;
import tasks.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager manager;
    private final Gson gson = Managers.getGson();
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTask);
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    public void handleTask(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String[] requestPath = (exchange.getRequestURI().getPath().split("/"));
            Optional<String> requestQuery = Optional.ofNullable(exchange.getRequestURI().getQuery());

            Endpoint endpoint = getEndpoint(method, requestPath, requestQuery);

            switch (endpoint) {
                case GetTasks:
                    List<Task> tasks = manager.getAllTasks();
                    if (tasks.size() == 0) {
                        System.out.println("Список задач пуст");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получен список задач");
                        sendText(exchange, gson.toJson(tasks));
                    }
                    break;
                case GetTaskById:
                    Task task = manager.getTaskById(getId(requestQuery.get()));
                    if (task != null) {
                        System.out.println("Получена задача с id: " + task.getId());
                        sendText(exchange, gson.toJson(task));
                    } else {
                        System.out.println("Некорректный id для задачи: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case PostTask:
                    String strTask = readText(exchange);
                    if (strTask.isEmpty()) {
                        System.out.println("Не передана задача");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Task taskToPost = gson.fromJson(strTask, Task.class);
                    Task oldTask = manager.getTaskById(taskToPost.getId());
                    if (oldTask != null) {
                        try {
                            manager.updateTask(taskToPost);
                            System.out.println("Задача с id: " + taskToPost.getId() + " обновлена");
                            exchange.sendResponseHeaders(200, 0);
                        } catch (TimeValidateException e) {
                            System.out.println("Задача пересеклась по времени");
                            exchange.sendResponseHeaders(405, 0);
                        }
                        return;
                    } else {
                        try {
                            manager.addTask(taskToPost);
                            System.out.println("Задачa добавлена");
                            exchange.sendResponseHeaders(200, 0);
                        } catch (TimeValidateException e) {
                            System.out.println("Это время занято другой задачей");
                            exchange.sendResponseHeaders(405, 0);
                        }
                    }
                    break;
                case DeleteTasks:
                    manager.clearAllTasks();
                    System.out.println("Все задачи удалены");
                    exchange.sendResponseHeaders(200, 0);
                    break;
                case DeleteTaskById:
                    Task taskToDel = manager.getTaskById(getId(requestQuery.get()));
                    if (taskToDel != null) {
                        manager.deleteTaskById(taskToDel.getId());
                        System.out.println("Удалена задача с id: " + taskToDel.getId());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Некорректный id для задачи: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case GetSubTasks:
                    List<SubTask> subtasks = manager.getAllSubTasks();
                    if (subtasks.size() == 0) {
                        System.out.println("Список подзадач пуст");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получен список подзадач");
                        sendText(exchange, gson.toJson(subtasks));
                    }
                    break;
                case GetSubTaskById:
                    SubTask sub = manager.getSubTaskById(getId(requestQuery.get()));
                    if (sub != null) {
                        System.out.println("Получена подзадача с id: " + sub.getId());
                        sendText(exchange, gson.toJson(sub));
                    } else {
                        System.out.println("Некорректный id для подзадачи: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case PostSubTask:
                    String strSub = readText(exchange);
                    if (strSub.isEmpty()) {
                        System.out.println("Не передана задача");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    SubTask subToPost = gson.fromJson(strSub, SubTask.class);
                    SubTask oldSub = manager.getSubTaskById(subToPost.getId());
                    if (oldSub != null) {
                        try {
                            manager.updateSubTask(subToPost);
                            System.out.println("Подзадача с id: " + subToPost.getId() + " обновлена");
                            exchange.sendResponseHeaders(200, 0);
                        } catch (TimeValidateException e) {
                            System.out.println("Подзадача пересеклась по времени");
                            exchange.sendResponseHeaders(405, 0);
                        }
                        return;
                    } else {
                        try {
                            manager.addSubTask(subToPost);
                            System.out.println("Подзадачa добавлена");
                            exchange.sendResponseHeaders(200, 0);
                        } catch (TimeValidateException e) {
                            System.out.println("Это время занято другой задачей");
                            exchange.sendResponseHeaders(405, 0);
                        }
                    }
                    break;
                case DeleteSubTasks:
                    manager.clearAllSubTasks();
                    System.out.println("Все подзадачи удалены");
                    exchange.sendResponseHeaders(200, 0);
                    break;
                case DeleteSubTaskById:
                    SubTask subToDel = manager.getSubTaskById(getId(requestQuery.get()));
                    if (subToDel != null) {
                        manager.deleteSubTaskById(subToDel.getId());
                        System.out.println("Удалена подзадача с id: " + subToDel.getId());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Некорректный id для подзадачи: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case GetEpics:
                    List<Epic> epics = manager.getAllEpics();
                    if (epics.size() == 0) {
                        System.out.println("Список эпиков пуст");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получен список эпиков");
                        sendText(exchange, gson.toJson(epics));
                    }
                    break;
                case GetEpicById:
                    Epic epic = manager.getEpicById(getId(requestQuery.get()));
                    if (epic != null) {
                        System.out.println("Получен эпик с id: " + epic.getId());
                        sendText(exchange, gson.toJson(epic));
                    } else {
                        System.out.println("Некорректный id для эпика: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case PostEpic:
                    String strEpic = readText(exchange);
                    if (strEpic.isEmpty()) {
                        System.out.println("Не передана задача");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Epic epicToPost = gson.fromJson(strEpic, Epic.class);
                    Epic oldEpic = manager.getEpicById(epicToPost.getId());
                    if (oldEpic != null) {
                        manager.updateEpic(epicToPost);
                        System.out.println("Эпик с id: " + epicToPost.getId() + " обновлен");
                        exchange.sendResponseHeaders(200, 0);
                        return;
                    } else {
                        manager.addEpic(epicToPost);
                        System.out.println("Эпик добавлен");
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                case DeleteEpics:
                    manager.clearAllEpics();
                    System.out.println("Все эпики удалены");
                    exchange.sendResponseHeaders(200, 0);
                    break;
                case DeleteEpicById:
                    Epic epicToDel = manager.getEpicById(getId(requestQuery.get()));
                    if (epicToDel != null) {
                        manager.deleteEpicById(epicToDel.getId());
                        System.out.println("Удален эпик с id: " + epicToDel.getId());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Некорректный id для эпика: " + requestQuery.get());
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case GetHistory:
                    List<Task> history = manager.getHistory();
                    if (history.size() == 0) {
                        System.out.println("История просмотров пустая");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получена история просмотров");
                        sendText(exchange, gson.toJson(history));
                    }
                    break;
                case getPrioritizedTasks:
                    List<Task> listTasks = manager.getPrioritizedTasks();
                    if (listTasks.size() == 0) {
                        System.out.println("Список задач пуст");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получен список приоритетных задач");
                        sendText(exchange, gson.toJson(listTasks));
                    }
                    break;
                case GetSubByEpic:
                    Epic epicWithSub = manager.getEpicById(getId(requestQuery.get()));
                    if (epicWithSub == null) {
                        System.out.println("Эпик с " + requestQuery.get() + " не существует");
                        exchange.sendResponseHeaders(404, 0);
                    } else {
                        System.out.println("Получены подзадачи эпика с " + requestQuery.get());
                        List<SubTask> subTaskByEpic = manager.getSubTaskByEpic(getId(requestQuery.get()));
                        sendText(exchange, gson.toJson(subTaskByEpic));
                    }
                    break;
                case Unknown:
                    System.out.println("Такой эндпоинт не существует");
                    exchange.sendResponseHeaders(405, 0);
                    break;
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
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

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int getId(String str) {
        try {
            String id = str.substring("id=".length());
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1; // получили неверный идентификатор
        }
    }
}