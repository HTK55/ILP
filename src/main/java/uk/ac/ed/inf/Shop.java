package uk.ac.ed.inf;

import java.util.List;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class Shop {
    private final String name;
    private final String location;
    private final List<MenuItem> menu;

    public Shop(String name, String location, List<MenuItem> menu) {
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }
}
