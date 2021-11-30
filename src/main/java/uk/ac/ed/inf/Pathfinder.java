package uk.ac.ed.inf;


import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Pathfinder {
    public final ArrayList<ArrayList<Line2D>> confinementZone;
    public final ArrayList<LongLat> landmarks;
    private ArrayList<Shop> shops;

    public Pathfinder(Website website, ArrayList<Shop> shops) {
        this.confinementZone = website.getConfinementZone();
        this.landmarks = website.getLandmarks();
        this.shops = shops;
    }

    public Shop getClosestShop(LongLat currLoc) {
        Shop closest = this.shops.get(0);
        double smallestDist = currLoc.distanceTo(this.shops.get(0).getLocation());
        for (Shop shop : this.shops) {
            double distance = currLoc.distanceTo(shop.getLocation());
            if (distance < smallestDist) {
                smallestDist = distance;
                closest = shop;
            }
        }
        return closest;
    }

    public void getClosestOrder(LongLat currLoc, ArrayList<Order> orders) {
        double smallestDist = currLoc.distanceTo(orders.get(0).getDeliveredFrom().get(0));
        Order closest = orders.get(0);
        for (Order order : orders) {
            for (LongLat stop : order.getDeliveredFrom()) {
                double distance = currLoc.distanceTo(stop);

            }
        }
    }

}
