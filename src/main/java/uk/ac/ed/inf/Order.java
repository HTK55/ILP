package uk.ac.ed.inf;

import java.util.*;

/**
 *  The Order class, used to collect and store all the information about each order the drone must deliver.
 */
public class Order {
    private final String orderNo;
    private ArrayList<String> itemsOrdered;
    /**
     * List of LongLat locations of the shops the order needs to be delivered from
     */
    private ArrayList<LongLat> deliveredFrom;
    /**
     * HashMap of shop names and their corresponding LongLat location the order is delivered from
     */
    private HashMap<String, LongLat> shops;
    private final LongLat deliveredTo;
    private final String w3wLocation;
    /**
     * Cost of the order
     */
    private int costInPence;


    /**
     * Class constructor.
     *
     * @param orderNo order number
     * @param deliveredTo LongLat location the order must be delivered to
     * @param w3wLocation w3w location the order must be delivered to
     */
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
        return Objects.equals(orderNo, order.orderNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNo);
    }
}
