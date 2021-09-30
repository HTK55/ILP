package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

public class Menus {
    public String name;
    public String port;

    private static final HttpClient client = HttpClient.newHttpClient();

    public Menus(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public int getDeliveryCost(String... strings) {
        int cost = 50;
        String urlString = "http://"+this.name+":"+this.port+"/menus/menus.json";
        // HttpRequest assumes that it is a GET request by default.
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            // We call the send method on the client which we created.
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Shop[] shops = new Gson().fromJson(response.body(), Shop[].class);
            for (String string : strings) {
                for (Shop shop : shops) {
                    for (MenuItem menuItem : shop.getMenu()) {
                        if (string.equals(menuItem.getItem())) {
                            cost += menuItem.getPence();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cost;
    }
}
