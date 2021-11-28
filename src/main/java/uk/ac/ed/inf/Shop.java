package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class Shop {
    private final String name;
    private final String location;
    private ArrayList<Order> orders;

    public Shop(String name, String location, ArrayList<Order> orders) {
        this.name = name;
        this.location = location;
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<Order> getMenu() {
        return orders;
    }
}
