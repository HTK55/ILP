package uk.ac.ed.inf;

import java.util.ArrayList;

public class Order {
    private String orderNo;
    private ArrayList<String> itemsOrdered;
    private ArrayList<LongLat> deliveredFrom;
    private ArrayList<String> shops;
    private LongLat deliveredTo;
    private String w3wLocation;
    private int costInPence;
    //private double distanceCost; maybe

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

    public void setShops(ArrayList<String> shops) {
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

    public ArrayList<String> getShops() {
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
}
