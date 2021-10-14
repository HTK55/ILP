package uk.ac.ed.inf;

/**
 * A class used for Gson parsing the information in each item in a shop's menu from the webserver. Contains the name of
 * the item on the menu and the price in pence of that item.
 */
public class MenuItem {
    private final String item;
    private final int pence;

    public MenuItem(String item, int pence) {
        this.item = item;
        this.pence = pence;
    }

    public String getItem() {
        return item;
    }

    public int getPence() {
        return pence;
    }
}
