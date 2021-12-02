package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class GsonShop {
    private final String name;
    private final String location;
    private final ArrayList<GsonMenuItem> menu;
    private HashMap<String, Integer> items;

    public GsonShop(String name, String location, ArrayList<GsonMenuItem> menu) {
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    /**
     * Puts the list of GsonMenuItems into a HashMap that the Website class better use to process data, where the
     * keys are the name of the item and the values are the cost.
     */
    public void setHashMap() {
        HashMap<String, Integer> items = new HashMap<>();
        for (GsonMenuItem item : this.menu) {
            items.put(item.getItem(),item.getPence());
        }
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }
}
