package com.alexanderdem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TokenLifetime {
    private HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private final URI tokenURI = URI.create("https://iiko.biz:9900/api/0/auth/access_token?user_id=SovyNezhnye_API&user_secret=m2jbp3SpVw4B");
    private URI dataURI;

    public static void main(String[] args) {
        new TokenLifetime();
    }

    private TokenLifetime() {
        dataURI = createDataUri();
        calculateLifetime();
    }

    private URI createDataUri() {
        String token = (String) getRequest(tokenURI).body();
        token = token.replace('"', ' ').trim();
        System.out.println("Получен токен");
        String dataURIString = ("https://iiko.biz:9900/api/0/organization/list?access_token=" +
                token +
                "&request_timeout=10000");
        return URI.create(dataURIString);
    }

    private HttpResponse getRequest(URI requestUri) {
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(requestUri)
                .build();
        HttpResponse response = null;
        try {
            response =  client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void calculateLifetime() {
        System.out.println("Измеряем время жизни токена в минутах:");
        int time = 0;
        while (getRequest(dataURI).statusCode() == 200) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            System.out.print(time + " ");
        }
        System.out.println("");
        System.out.println("---------------Ответ---------------");
        System.out.println("Время жизни токена = " + time + " минут");
        System.out.println("Полученные данные могут исказить в меньшую сторону задержки сети");
    }
}
