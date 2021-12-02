package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * The Shop class used to collect and store all the information about each shop that has orders that need to be
 * completed.
 */
public class Shop {
    private static final double HOVER_COST = 0.00015;

    private final String name;
    private final LongLat location;
    /**
     * TreeMap of orders, where the key is the travel cost of each order from this shop
     */
    private final TreeMap<Double,Order> orders;
    /**
     * TreeMap of travel costs, where the key is the order with that travel cost
     */
    private final TreeMap<String,Double> travelCosts;
    /**
     * TreeMap of LongLat locations the drone would to, to execute the order, where the key is the travel cost of each
     * order from this shop
     */
    private final TreeMap<Double,ArrayList<LongLat>> paths;


    /**
     * Class constructor.
     *
     * @param name shop name
     * @param location LongLat shop location
     */
    public Shop(String name, LongLat location) {
        this.name = name;
        this.location = location;
        this.orders = new TreeMap<>();
        this.travelCosts = new TreeMap<>();
        this.paths = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    public LongLat getLocation() {
        return location;
    }

    public TreeMap<Double, Order> getOrders() {
        return orders;
    }

    public TreeMap<Double, ArrayList<LongLat>> getPaths() {
        return paths;
    }

    /**
     * Adds an order that this shop has and calculates the path the drone would take to execute that order, as in if the
     * drone needs collect the delivery from another shop or avoid the no fly zone by visitng a landmark. Uses this
     * to calculate the travel cost of this - using the total distance of the path/the cost of the order. Then adds this
     * information to the class's maps.
     *
     * @param order order being added to the shop
     * @param noFlyZone current no fly zone
     * @param landmarks current list of landmarks
     */
    public void addOrder(Order order, ArrayList<ArrayList<Line2D>> noFlyZone, ArrayList<LongLat> landmarks) {
        double distance = 0;
        ArrayList<LongLat> path = new ArrayList<>();
        for (LongLat loc : order.getDeliveredFrom()) { //only 1 or 2 long so could be if statement but left in for expandability
            if (!loc.equals(this.getLocation())) { // if we wanted to add more shops, would use a treemap to sort by each distance from current shop, then calculate the paths and total distance
                distance = distance + this.getLocation().getTravelDistance(loc, noFlyZone, landmarks) + HOVER_COST;
                ArrayList<LongLat> locPaths = this.getLocation().getPath(loc, noFlyZone, landmarks);
                path.addAll(locPaths);
            }
        }
        ArrayList<LongLat> lastPaths;
        if (path.isEmpty()) {
            distance = distance + this.getLocation().getTravelDistance(order.getDeliveredTo(), noFlyZone, landmarks) + HOVER_COST;
            lastPaths = this.getLocation().getPath(order.getDeliveredTo(), noFlyZone, landmarks);
        }
        else {
            distance = distance + path.get(path.size() - 1).getTravelDistance(order.getDeliveredTo(), noFlyZone, landmarks) + HOVER_COST;
            lastPaths = path.get(path.size() - 1).getPath(order.getDeliveredTo(), noFlyZone, landmarks);
        }
        path.addAll(lastPaths);
        double travelCost = distance/order.getCostInPence();
        this.orders.put(travelCost, order);
        this.travelCosts.put(order.getOrderNo(), travelCost);
        this.paths.put(travelCost, path);
    }

    /**
     * Removes an order from the shops maps of orders when that order has been completed.
     *
     * @param order order to be removed
     */
    public void removeOrder(Order order) {
        if (travelCosts.containsKey(order.getOrderNo())) {
            Double key = travelCosts.get(order.getOrderNo());
            this.orders.remove(key);
            this.paths.remove(key);
        }
    }
}
