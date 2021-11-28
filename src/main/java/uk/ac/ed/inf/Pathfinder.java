package uk.ac.ed.inf;


import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Pathfinder {
    public final ArrayList<ArrayList<Line2D>> confinementZone;

    public Pathfinder(Website website) {
        this.confinementZone = website.getConfinementZone();
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

    public boolean crossesConfineZone(LongLat from, LongLat to) {
        Line2D move = new Line2D.Double(from.longitude,from.latitude,to.longitude,to.latitude);
        for (ArrayList<Line2D> polygon : this.confinementZone) {
            for (Line2D line : polygon) {
                if (move.intersectsLine(line)) {
                    return true;
                }
            }
        }
        return false;
    }
}
