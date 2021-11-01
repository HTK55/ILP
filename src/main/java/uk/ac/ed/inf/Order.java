package uk.ac.ed.inf;

import java.util.ArrayList;

public class Order {
    private String orderNo;
    private ArrayList<String> itemsOrdered;
    private ArrayList<String> deliveredFrom;
    private String deliveredTo;
    private int costInPence;

    public Order(String orderNo, ArrayList<String> itemsOrdered) {
        this.orderNo = orderNo;
        this.itemsOrdered = itemsOrdered;
    }

    public void setDeliveredFrom(ArrayList<String> deliveredFrom) {
        this.deliveredFrom = deliveredFrom;
    }

    public void setDeliveredTo(String deliveredTo) {
        this.deliveredTo = deliveredTo;
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

    public ArrayList<String> getDeliveredFrom() {
        return deliveredFrom;
    }

    public String getDeliveredTo() {
        return deliveredTo;
    }

    public int getCostInPence() {
        return costInPence;
    }
}
