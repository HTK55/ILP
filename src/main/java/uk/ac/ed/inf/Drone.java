package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import static uk.ac.ed.inf.App.HOME_LOCATION;

/**
 * The Drone class, used to move and store information about the drone. Contains methods to calculate, deliver  and
 * record the optimal sequence of orders.
 */
public class Drone {
    /**
     * the angle key to set the drone to hover, essentially a value we would never legitimately input to move the drone
     * in that direction
     */
    private static final int HOVER = -999;
    /**
     * the length of a drone move in degrees in a straight line
     */
    private static final double DIST_MOVED = 0.00015;
    /**
     * the maximum number of moves the drone can make
     */
    private static final int MAX_DRONE_MOVES = 1500;


    private int movesLeft;
    /**
     * the current location of the drone
     */
    private LongLat currLoc;
    private final ArrayList<Order> orders;
    private final ArrayList<Order> completedOrders;
    /**
     * HashMap of shop names and their corresponding shops that have orders that need to be completed
     */
    private final HashMap<String,Shop> shops;
    /**
     * LongLat locations the drone has been to, used to map the geojson path of the drone
     */
    private final ArrayList<LongLat> beenTo;
    private final Derby database;
    private ArrayList<ArrayList<Line2D>> noFlyZone;
    private ArrayList<LongLat> landmarks;


    /**
     * Class constructor
     *
     * @param currLoc the LongLat location where the drone starts
     * @param orders the list of Orders the drone needs to complete
     * @param shops a HashMap record of all the shops the drone must pick up deliveries from, where the key is name of the shop
     * @param database the database client the drone will use to record its flightpath and the deliveries it makes
     */
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

    public void setNoFlyZone(ArrayList<ArrayList<Line2D>> noFlyZone) {
        this.noFlyZone = noFlyZone;
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
     * Calculates the new location of the drone after moving at a specified angle.
     *
     * @param angle angle the drone moves at
     * @return new LongLat location
     */
    private LongLat calculatePosition(int angle) {
        double angleInRadians = Math.toRadians(angle);
        double newLongitude = this.getCurrLoc().getLongitude() + Math.cos(angleInRadians) * DIST_MOVED;
        double newLatitude = this.getCurrLoc().getLatitude() + Math.sin(angleInRadians) * DIST_MOVED;
        return new LongLat(newLongitude, newLatitude);
    }

    /**
     * Calculates the next position of the drone when given an angle, and saves this as a move in the flightpath table
     * in the database, using the current location and the new location calculated. We use the convention that 0 means
     * go East, 90 means go North, 180 means go West, and 270 means go South, with the other multiples of ten between 0
     * and 350 representing the obvious directions between these four major compass directions. The angle can also be
     * set as the HOVER constant, which we use if we want the drone to hover in place. Throws an exception if the input
     * angle is not valid.
     *
     * @param angle the direction we want the drone to move in, must be either the HOVER constant or a multiple of 10
     *             between 0 and 350
     * @param order the Order being completed in this move
     * @return a new LongLat object at the position the drone is in after the move
     */
    private LongLat nextPosition(int angle, Order order) {
        LongLat newLoc;
        if (angle == HOVER) {
            newLoc = this.currLoc;
        } else {
            newLoc = calculatePosition(angle);
            while (this.currLoc.crossesNoFlyZone(newLoc, this.noFlyZone)) {
                angle = (angle + 10) % 360;
                newLoc = calculatePosition(angle);
            }
            setCurrLoc(newLoc);
        }
        this.setMovesLeft(this.getMovesLeft() - 1);
        this.database.addToFlightPath(order.getOrderNo(), this.getCurrLoc(), angle, newLoc);
        return newLoc;
    }

    /**
     * Moves the drone from where it currently through a given path, calculating the correct angle (a multiple of 10
     * between 0 and 350) the drone must travel at to get to each location in the path. Also sets the drone to hover at
     * shops or delivery locations it visits.
     *
     * @param path a list of LongLats the drone must travel to
     * @param order the Order the drone is currently completely
     */
    public void moveTo(ArrayList<LongLat> path, Order order) {
        for (LongLat nextLoc : path) {
            while (!this.getCurrLoc().closeTo(nextLoc)) {
                double angle = Math.toDegrees(Math.atan2(nextLoc.getLatitude() - this.getCurrLoc().getLatitude(), nextLoc.getLongitude() - this.getCurrLoc().getLongitude()));
                angle = (Math.round((angle + 360) / 10) * 10) % 360;
                LongLat newLoc = nextPosition((int) angle, order);
                this.addToBeenTo(newLoc);
                this.setCurrLoc(newLoc);
            }
            // hover if the drone is at a shop or delivery location, as in not a landmark of the home location
            if (!this.landmarks.contains(nextLoc) || !nextLoc.equals(HOME_LOCATION)) {
                nextPosition(-999, order);
            }
        }
    }

    /**
     * Calculates the shop with an order with the best travel cost (distance/cost of order), that will minimise the
     * travel cost if we move to that shop.
     *
     * @return the Shop with the best travel cost
     */
    public Shop getBestShop() {
        double smallest = 999999999; //dummy distance gets replaced immediately
        Shop closest = new Shop("Home", HOME_LOCATION); //if no smallest distance is found go back to appleton
        for (Shop shop : this.shops.values()) {
            if (!shop.getOrders().isEmpty()) {
                double travelCost = (((getCurrLoc().getTravelDistance(shop.getLocation(), this.noFlyZone, this.landmarks) + DIST_MOVED) / shop.getOrders().firstEntry().getValue().getCostInPence())) + shop.getOrders().firstKey();
                if (travelCost < smallest && !shop.getOrders().isEmpty()) {
                    smallest = travelCost;
                    closest = shop;
                }
            }
        }
        return closest;
    }

    /**
     * Calculates how many moves it would take for the drone to get back from its current location to the home location
     * by calculating the travel distance and dividing it by the length of a move.
     *
     * @return number of moves to home location
     */
    public long movesToHome(){
        return Math.round(getCurrLoc().getTravelDistance(HOME_LOCATION, this.noFlyZone, this.landmarks)/DIST_MOVED);
    }
}
