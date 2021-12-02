package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Drone {
    // the angle key to set the drone to hover, essentially a value we would never legitimately input to move the drone in that direction
    private static final int HOVER = -999;
    // the length of a drone move in degrees in a straight line
    private static final double DIST_MOVED = 0.00015;
    private static final LongLat APPLETON_TOWER = new LongLat(-3.186874, 55.944494);
    private static final int MAX_DRONE_MOVES = 1500;

    private int movesLeft;
    private LongLat currLoc;
    private final ArrayList<Order> orders;
    private final ArrayList<Order> completedOrders;
    private final HashMap<String,Shop> shops;
    private final ArrayList<LongLat> beenTo;
    private final Derby database;
    private ArrayList<ArrayList<Line2D>> confinementZone;
    private ArrayList<LongLat> landmarks;

    public Drone(LongLat currLoc, ArrayList<Order> orders, HashMap<String,Shop> shops, Derby database) {
        this.currLoc = currLoc;
        this.orders = orders;
        this.shops = shops;
        this.movesLeft = MAX_DRONE_MOVES;
        this.completedOrders = new ArrayList<>();
        this.beenTo = new ArrayList<>();
        this.beenTo.add(currLoc);
        this.database = database;
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

    public void setCurrLoc(LongLat currLoc) {
        this.currLoc = currLoc;
    }

    public void setConfinementZone(ArrayList<ArrayList<Line2D>> confinementZone) {
        this.confinementZone = confinementZone;
    }

    public void setLandmarks(ArrayList<LongLat> landmarks) {
        this.landmarks = landmarks;
    }

    public void addToBeenTo(LongLat longLat) {
        this.beenTo.add(longLat);
    }

    public void addCompletedOrder(Order completedOrder) {
        this.completedOrders.add(completedOrder);
    }

    public void removeOrder(Order completedOrder) {
        this.orders.remove(completedOrder);
    }

    public void removeOrderFromShops(Order completedOrder) {
        for (Shop shop : this.shops.values()) {
            shop.removeOrder(completedOrder);
        }
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public LongLat getCurrLoc() {
        return currLoc;
    }

    public ArrayList<Order> getCompletedOrders() {
        return completedOrders;
    }

    public ArrayList<LongLat> getBeenTo() {
        return beenTo;
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
    public LongLat nextPosition(int angle, Order order) {
        if (angle == HOVER) {
            this.setMovesLeft(this.getMovesLeft() - 1);
            //maybe change method to void and setCurrLoc to new LongLat
            this.database.addToFlightPath(order.getOrderNo(), this.getCurrLoc(), angle, this.getCurrLoc());
            return this.getCurrLoc();
        } else if (angle >= 0 && angle <= 350 && angle % 10 == 0) {
            double angleInRadians = Math.toRadians(angle);
            double newLongitude = this.getCurrLoc().getLongitude() + Math.cos(angleInRadians) * DIST_MOVED;
            double newLatitude = this.getCurrLoc().getLatitude()+ Math.sin(angleInRadians) * DIST_MOVED;
            this.setMovesLeft(this.getMovesLeft() - 1);
            LongLat newLoc = new LongLat(newLongitude, newLatitude);
            this.database.addToFlightPath(order.getOrderNo(), this.getCurrLoc(), angle, newLoc);
            return newLoc;
        } else {
            throw new IllegalArgumentException("This is not a valid angle");
        }
    }

    public void moveTo(ArrayList<LongLat> path, Order order) {
        for (LongLat nextLoc : path) {
            while (!this.getCurrLoc().closeTo(nextLoc)) {
                double angle = Math.toDegrees(Math.atan2(nextLoc.getLatitude() - this.getCurrLoc().getLatitude(), nextLoc.getLongitude() - this.getCurrLoc().getLongitude()));
                angle = (Math.round((angle + 360) / 10) * 10) % 360;
                LongLat newLoc = nextPosition((int) angle, order);
                this.addToBeenTo(newLoc);
                this.setCurrLoc(newLoc);
            }
            if (!this.landmarks.contains(nextLoc) || !nextLoc.equals(APPLETON_TOWER)) {
                nextPosition(-999, order);
            }
        }
    }

    public Shop getBestShop() {
        double smallest = 999999999; //dummy distance gets replaced immediately
        Shop closest = new Shop("Appleton Tower", APPLETON_TOWER); //if no smallest distance is found go back to appleton
        for (Shop shop : this.shops.values()) {
            if (!shop.getOrders().isEmpty()) {
                double travelCost = (((getCurrLoc().getTravelDistance(shop.getLocation(), this.confinementZone, this.landmarks) + DIST_MOVED) / shop.getOrders().firstEntry().getValue().getCostInPence())) + shop.getOrders().firstKey();
                if (travelCost < smallest && !shop.getOrders().isEmpty()) {
                    smallest = travelCost;
                    closest = shop;
                }
            }
        }
        return closest;
    }

    public long movesToAppleton(){
        return Math.round(getCurrLoc().getTravelDistance(APPLETON_TOWER, this.confinementZone, this.landmarks)/DIST_MOVED);
    }
}
