package uk.ac.ed.inf;

import java.util.ArrayList;

public class Order {
    private String orderNo;
    private ArrayList<String> itemsOrdered;
    private ArrayList<LongLat> deliveredFrom;
    private LongLat deliveredTo;
    private int costInPence;

    public Order(String orderNo, LongLat deliveredTo) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
    }

    public void setItemsOrdered(ArrayList<String> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }

    public void setDeliveredFrom(ArrayList<LongLat> deliveredFrom) {
        this.deliveredFrom = deliveredFrom;
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

    public ArrayList<LongLat> getDeliveredFrom() {
        return deliveredFrom;
    }

    public LongLat getDeliveredTo() {
        return deliveredTo;
    }

    public int getCostInPence() {
        return costInPence;
    }
}
