package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Drone {
    // the angle key to set the drone to hover, essentially a value we would never legitimately input to move the drone in that direction
    static final int HOVER = -999;
    // the length of a drone move in degrees in a straight line
    static final double DIST_MOVED = 0.00015;
    public static final LongLat APPLETONTOWER = new LongLat(-3.186874, 55.944494);
    public static final int MAX_DRONE_MOVES = 1500;

    public int movesLeft;
    public LongLat currLoc;
    public ArrayList<Order> orders;
    public ArrayList<Order> completedOrders;
    private ArrayList<Shop> shops;

    public Drone(LongLat currLoc, ArrayList<Order> orders, ArrayList<Shop> shops) {
        this.currLoc = currLoc;
        this.orders = orders;
        this.shops = shops;
        this.movesLeft = MAX_DRONE_MOVES;
        this.completedOrders = new ArrayList<>();
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

    public void setCurrLoc(LongLat currLoc) {
        this.currLoc = currLoc;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void addCompletedOrder(Order completedOrder) {
        this.completedOrders.add(completedOrder);
    }

    public void removeOrder(Order completedOrder) {
        if (this.completedOrders.contains(completedOrder)) {
            this.completedOrders.remove(completedOrder);
        }
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public LongLat getCurrLoc() {
        return currLoc;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public ArrayList<Order> getCompletedOrders() {
        return completedOrders;
    }

    /**
     * Calculates the next position of the drone when given an angle. We use the convention that 0 means go East,
     * 90 means go North, 180 means go West, and 270 means go South, with the other multiples of ten between 0 and
     * 350 representing the obvious directions between these four major compass directions. The angle can also be
     * set as the HOVER constant, which we use if we want the drone to hover in place. Throws an exception if the input
     * angle is not valid.
     *
     * @param angle the direction we want the drone to move in, must be either the HOVER constant or a multiple of 10 between 0 and 350
     * @return a new LongLat object at the position the drone is in after the move
     * @throws IllegalArgumentException if the angle given is not valid
     */
    public LongLat nextPosition(int angle) {
        if (angle == HOVER) {
            return this.getCurrLoc(); //maybe change method to void and setCurrLoc to new LongLat
        } else if (angle >= 0 && angle <= 350 && angle % 10 == 0) {
            double angleInRadians = Math.toRadians(angle);
            double newLongitude = this.getCurrLoc().getLongitude() + Math.cos(angleInRadians) * DIST_MOVED;
            double newLatitude = this.getCurrLoc().getLatitude()+ Math.sin(angleInRadians) * DIST_MOVED;
            this.setMovesLeft(this.getMovesLeft() - 1);
            return new LongLat(newLongitude, newLatitude);
        } else {
            throw new IllegalArgumentException("This is not a valid angle");
        }
    }

    public void moveTo(LongLat nextLoc, Order order, Derby derbyClient) {
        while(!this.getCurrLoc().closeTo(nextLoc)) {
            double angle = Math.toDegrees(Math.atan2(nextLoc.getLatitude() - this.getCurrLoc().getLatitude(), nextLoc.getLongitude() - this.getCurrLoc().getLongitude()));
            angle = (angle + 360) % 360;
            angle = Math.round(angle/10) * 10;
            LongLat newLoc = nextPosition((int)angle);
            //derbyClient.
            this.setCurrLoc(newLoc);
        }
    }

    public Shop getClosestShop(ArrayList<ArrayList<Line2D>> confinementZone, ArrayList<LongLat> landmarks) {
        Shop closest = this.shops.get(0);
        double smallestDist = getCurrLoc().getTravelDistance(this.shops.get(0).getLocation(), confinementZone, landmarks);
        for (Shop shop : this.shops) {
            double distance = getCurrLoc().getTravelDistance(shop.getLocation(), confinementZone, landmarks);
            if (distance < smallestDist) {
                smallestDist = distance;
                closest = shop;
            }
        }
        return closest;
    }

    public double movesToAppleton(ArrayList<ArrayList<Line2D>> confinementZone, ArrayList<LongLat> landmarks){
        return getCurrLoc().getTravelDistance(APPLETONTOWER,confinementZone, landmarks)/DIST_MOVED;
    }
}
