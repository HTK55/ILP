package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class Shop {
    private final String name;
    private final LongLat location;
    private final TreeMap<Integer,Order> orders;
    private final TreeMap<String,Integer> reverseOrders;
    private final TreeMap<Integer,ArrayList<LongLat>> paths;

    public Shop(String name, LongLat location) {
        this.name = name;
        this.location = location;
        this.orders = new TreeMap<>();
        this.reverseOrders = new TreeMap<>();
        this.paths = new TreeMap<>();
    }

    public void addOrder(Order order, ArrayList<ArrayList<Line2D>> confinementZone, ArrayList<LongLat> landmarks) {
        double distance = 0;
        ArrayList<LongLat> path = new ArrayList<>();
        for (LongLat loc : order.getDeliveredFrom()) { //only 1 or 2 long so could be if statement but left in for expandability
            if (!loc.equals(this.getLocation())) { // if we wanted to add more shops, would use a treemap to sort by each distance from current shop, then calculate the paths and total distance
                distance = distance + this.getLocation().getTravelDistance(loc, confinementZone, landmarks) + 0.00015; //have to hover
                ArrayList<LongLat> locPaths = this.getLocation().getPath(loc, confinementZone, landmarks);
                path.addAll(locPaths);
            }
        }
        ArrayList<LongLat> lastPaths;
        if (path.isEmpty()) {
            distance = distance + this.getLocation().getTravelDistance(order.getDeliveredTo(), confinementZone, landmarks) + 0.00015;
            lastPaths = this.getLocation().getPath(order.getDeliveredTo(), confinementZone, landmarks);
        }
        else {
            distance = distance + path.get(path.size() - 1).getTravelDistance(order.getDeliveredTo(), confinementZone, landmarks) + 0.00015;
            lastPaths = path.get(path.size() - 1).getPath(order.getDeliveredTo(), confinementZone, landmarks);
        }
        path.addAll(lastPaths);
        double travelCost = distance/order.getCostInPence();
        travelCost = travelCost * 1000000000;
        this.orders.put((int)travelCost, order);
        this.reverseOrders.put(order.getOrderNo(), (int)travelCost);
        this.paths.put((int)travelCost, path);
    }

    public void removeOrder(Order order) {
        if (reverseOrders.containsKey(order.getOrderNo())) {
            Integer key = reverseOrders.get(order.getOrderNo());
            this.orders.remove(key);
            this.paths.remove(key);
        }
    }

    public String getName() {
        return name;
    }

    public LongLat getLocation() {
        return location;
    }

    public TreeMap<Integer, Order> getOrders() {
        return orders;
    }

    public TreeMap<Integer, ArrayList<LongLat>> getPaths() {
        return paths;
    }
}
