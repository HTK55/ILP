package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
 */
public class Shop {
    private final String name;
    private final LongLat location;
    private TreeMap<Integer,Order> orders;
    private TreeMap<Integer,ArrayList<LongLat>> paths;

    public Shop(String name, LongLat location) {
        this.name = name;
        this.location = location;
        this.orders = new TreeMap<Integer,Order>();
        this.paths = new TreeMap<Integer,ArrayList<LongLat>>();
    }

    public void addOrder(Order order, ArrayList<ArrayList<Line2D>> confinementZone, ArrayList<LongLat> landmarks) {
        double distance = 0;
        ArrayList<LongLat> path = new ArrayList<>();
        for (LongLat loc : order.getDeliveredFrom()) { //only 1 or 2 long so could be if statement but left in for expandability
            if (!loc.equals(this.getLocation())) { // if we wanted to add more shops, would use a treemap to sort by each distance from current shop, then calculate the paths and total distance
                distance += this.getLocation().getTravelDistance(loc, confinementZone, landmarks);
                ArrayList<LongLat> locPaths = this.getLocation().getPath(loc, confinementZone, landmarks);
                if (locPaths.size() == 1) {
                    path.add(locPaths.get(0));
                } else {
                    for (LongLat locPath : locPaths) {
                        path.add(locPath);
                    }
                }
            }
        }
        ArrayList<LongLat> lastPaths = path.get(path.size() - 1).getPath(order.getDeliveredTo(), confinementZone, landmarks);
        if (lastPaths.size() == 1) {
            path.add(lastPaths.get(0));
        }
        else {
            for (LongLat lastPath : lastPaths) {
                path.add(lastPath);
            }
        }
        Integer travelCost = (int)distance/order.getCostInPence();
        this.orders.put((int)travelCost,order);
        this.paths.put((int)travelCost,path);
    }

    public void removeOrder(Order order) {
        for( Map.Entry<Integer,Order> entry : this.getOrders().entrySet()){
            if (entry.getValue().equals(order)) {
                this.orders.remove(entry.getKey());
                this.paths.remove(entry.getKey());
            }
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
