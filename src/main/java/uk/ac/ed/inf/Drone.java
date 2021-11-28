package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Drone {
    // the angle key to set the drone to hover, essentially a value we would never legitimately input to move the drone in that direction
    static final int HOVER = -999;
    // the length of a drone move in degrees in a straight line
    static final double DIST_MOVED = 0.00015;

    public int movesLeft;
    public LongLat currLoc;
    public ArrayList<Order> orders;
    public ArrayList<Order> completedOrders;

    public Drone(LongLat currLoc, ArrayList<Order> orders) {
        this.currLoc = currLoc;
        this.orders = orders;
        this.movesLeft = 1500;
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

    public void setCompletedOrders(ArrayList<Order> completedOrders) {
        this.completedOrders = completedOrders;
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
            double newLongitude = this.getCurrLoc().longitude + Math.cos(angleInRadians) * DIST_MOVED;
            double newLatitude = this.getCurrLoc().latitude + Math.sin(angleInRadians) * DIST_MOVED;
            return new LongLat(newLongitude, newLatitude);
        } else {
            throw new IllegalArgumentException("This is not a valid angle");
        }
    }
}
