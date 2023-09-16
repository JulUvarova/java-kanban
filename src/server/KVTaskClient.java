package server;

import exceptions.RequestException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String uri;
    private String token;
    private HttpClient client;

    public KVTaskClient(String uri){
        this.uri = uri;
        client = HttpClient.newHttpClient();
        register(uri);
    }

    public void register(String uri) {
        try {
            HttpRequest.Builder rb = HttpRequest.newBuilder();
            HttpRequest request = rb
                    .GET()
                    .uri(URI.create(uri + "/register"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                token = response.body().toString();
                System.out.println("Получен токен: " + token);
            } else {
                throw new RequestException("Ошибка регистрации. Код ответа от KVServer: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Невозможно выполнить запрос: " + e.getMessage());
        }
    }

    public void put(String key, String value) {
        try {
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
            HttpRequest.Builder rb = HttpRequest.newBuilder();
            HttpRequest request = rb
                    .POST(body)
                    .uri(URI.create(uri + "/save/" + key + "?API_TOKEN=" + token))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Данные по ключу " + key + " сохранены");
            } else {
                throw new RequestException("Ошибка сохранения. Код ответа от KVServer: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Невозможно выполнить запрос: " + e.getMessage());
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri + "/load/" + key + "?API_TOKEN=" + token))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Данные по ключу " + key + " загружены");
                return response.body();
            } else {
                throw new RequestException("Ошибка загрузки. Код ответа от KVServer: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Невозможно выполнить запрос: " + e.getMessage());
        }
    }
}
