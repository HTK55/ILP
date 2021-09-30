package uk.ac.ed.inf;

import java.util.ArrayList;

public class Shop {
    private String name;
    private String location;
    private ArrayList<MenuItem> menu;

    public Shop(String name, String location, ArrayList<MenuItem> menu) {
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

    public ArrayList<MenuItem> getMenu() {
        return menu;
    }
}
