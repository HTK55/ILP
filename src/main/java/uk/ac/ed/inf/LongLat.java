package uk.ac.ed.inf;

/**
 * The LongLat class, used to represent a point on the map. Contains various methods to calculate values relating to locations.
 */
public class LongLat {
    // the upper longitude boundary of the confinement zone
    static final double UPPER_LONG = -3.184319;
    // the lower longitude boundary of the confinement zone
    static final double LOWER_LONG = -3.192473;
    // the upper latitude boundary of the confinement zone
    static final double UPPER_LAT = 55.946233;
    // the lower latitude boundary of the confinement zone
    static final double LOWER_LAT = 55.942617;

    // the angle key to set the drone to hover, essentially a value we would never legitimately input to move the drone in that direction
    static final int HOVER = -999;

    // the distance tolerance in degrees, specifically a point is close to another point if the distance between them is strictly less than this value
    static final double DIST_TOL = 0.00015;

    // the length of a drone move in degrees in a straight line
    static final double DIST_MOVED = 0.00015;

    public double longitude;
    public double latitude;

    /**
     * Class constructor.
     *
     * @param longitude the longitude of the point being created
     * @param latitude the latitude of the point being created
     */
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Determines whether this LongLat object is within the confinement zone.
     *
     * @return true if the LongLat is within the confinement zone
     */
    public boolean isConfined() {
        return (this.longitude < UPPER_LONG && this.longitude > LOWER_LONG && this.latitude > LOWER_LAT && this.latitude < UPPER_LAT);
    }

    /**
     * Calculates the distance between this LongLat object and another LongLat object.
     *
     * @param longLat2 a second LongLat object we want to calculate the distance to
     * @return the distance between the two LongLats
     */
    public double distanceTo(LongLat longLat2) {
        return Math.sqrt(Math.pow((this.longitude - longLat2.longitude), 2) + Math.pow((this.latitude - longLat2.latitude), 2));
    }

    /**
     * Calculates if this LongLat object is "close to" another LongLat object. We define two points as being close to
     * each other if the distance between them is strictly less than the distance tolerance constant.
     *
     * @param longLat2 a second LongLat object that we want to know if our LongLat object is close to
     * @return true if the two LongLats are close to each other
     */
    public boolean closeTo(LongLat longLat2) {
        return (this.distanceTo(longLat2) < DIST_TOL);
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
            return this;
        } else if (angle >= 0 && angle <= 350 && angle % 10 == 0) {
            double angleInRadians = Math.toRadians(angle);
            double newLongitude = this.longitude + Math.cos(angleInRadians) * DIST_MOVED;
            double newLatitude = this.latitude + Math.sin(angleInRadians) * DIST_MOVED;
            return new LongLat(newLongitude, newLatitude);
        } else {
            throw new IllegalArgumentException("This is not a valid angle");
        }
    }
}
