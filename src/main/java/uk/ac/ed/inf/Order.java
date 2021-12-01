package uk.ac.ed.inf;

import java.util.*;

public class Order {
    private final String orderNo;
    private ArrayList<String> itemsOrdered;
    private ArrayList<LongLat> deliveredFrom;
    private HashMap<String, LongLat> shops;
    private final LongLat deliveredTo;
    private final String w3wLocation;
    private int costInPence;

    public Order(String orderNo, LongLat deliveredTo, String w3wLocation) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.w3wLocation = w3wLocation;
    }

    public void setItemsOrdered(ArrayList<String> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }

    public void setDeliveredFrom(ArrayList<LongLat> deliveredFrom) {
        this.deliveredFrom = deliveredFrom;
    }

    public void setShops(HashMap<String, LongLat> shops) {
        this.shops = shops;
    }

    public void setCostInPence(int costInPence) {
        this.costInPence = costInPence;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public ArrayList<String> getItemsOrdered() {
        return itemsOrdered;
    }

    public HashMap<String, LongLat> getShops() {
        return shops;
    }

    public ArrayList<LongLat> getDeliveredFrom() {
        return deliveredFrom;
    }

    public LongLat getDeliveredTo() {
        return deliveredTo;
    }

    public String getW3wLocation() {
        return w3wLocation;
    }

    public int getCostInPence() {
        return costInPence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return costInPence == order.costInPence && Objects.equals(orderNo, order.orderNo) && Objects.equals(itemsOrdered, order.itemsOrdered) && Objects.equals(deliveredFrom, order.deliveredFrom) && Objects.equals(shops, order.shops) && Objects.equals(deliveredTo, order.deliveredTo) && Objects.equals(w3wLocation, order.w3wLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNo, itemsOrdered, deliveredFrom, shops, deliveredTo, w3wLocation, costInPence);
    }
}
