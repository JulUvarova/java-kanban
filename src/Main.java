import server.KVServer;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
//        HttpTaskServer server1 = new HttpTaskServer();
//        server1.start();
//        Gson gson = Managers.getGson();
//        Task task = new Task("Задача", "описание", TaskStatus.NEW,
//                LocalDateTime.of(2023, 8, 13, 19, 0), 120);
//        Epic epic1 = new Epic("ПервыйПолный", "");
//        SubTask subTaskNew = new SubTask("Новая", "описание", TaskStatus.NEW, 2,
//                LocalDateTime.of(2023, 9, 13, 19, 0), 120);
//        manager.addTask(task);
//        manager.addEpic(epic1);
//        manager.addSubTask(subTaskNew);
//        manager.getEpicById(2);
//        HttpClient client = HttpClient.newHttpClient();
//        String json = gson.toJson(task);
//        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        final HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/tasks/task/"))
//                .POST(body)
//                .build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
