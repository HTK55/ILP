package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class GsonShop {
    private final String name;
    private final String location;
    private final ArrayList<GsonMenuItem> menu;

    public GsonShop(String name, String location, ArrayList<GsonMenuItem> menu) {
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

    public ArrayList<GsonMenuItem> getMenu() {
        return menu;
    }
}
