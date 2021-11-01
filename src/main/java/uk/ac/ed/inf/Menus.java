package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

/**
 * The Menus class, used to interface with the webserver to get information about shop menus. Contains methods to use
 * the information retrieved from the webserver in regard to shop menus.
 */
public class Menus {
    // HttpClient for http requests (should be moved to a higher file when implementing Menus http requests in future classes, as should only be one)
    private static final HttpClient client = HttpClient.newHttpClient();

    // Standard delivery charge for orders
    static final int STD_CHARGE = 50;

    public final String name;
    public final String port;

    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the webserver is running
     */
    public Menus(String name, String port) {
        this.name = name;
        this.port = port;
    }

    /**
     * Calculates the delivery cost when given some a set of items to be ordered. This is calculated by adding the
     * standard delivery cost to the price of the items, which we get by accessing the webserver. Fails if the server
     * cannot be accessed.
     *
     * @param order the items wanting to be delivered
     * @return the delivery cost in pence for the items ordered, if no items are available to be ordered, returns 0
     */
    public Order getDelivery(Order order) {
        int cost = STD_CHARGE;
        ArrayList<String> deliveredFrom = new ArrayList<>();
        String urlString = "http://"+this.name+":"+this.port+"/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            List<Shop> shops = new Gson().fromJson(response.body(), new TypeToken<List<Shop>>(){}.getType());
            for (String item : order.getItemsOrdered()) {
                for (Shop shop : shops) {
                    for (MenuItem menuItem : shop.getMenu()) {
                        if (item.equals(menuItem.getItem())) {
                            cost += menuItem.getPence();
                            deliveredFrom.add(shop.getLocation());
                        }
                    }
                }
            }
            order.setCostInPence(cost);
            order.setDeliveredFrom(deliveredFrom);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }
}
