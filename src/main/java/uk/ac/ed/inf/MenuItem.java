package uk.ac.ed.inf;

public class MenuItem {
    private String item;
    private int pence;

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
