package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The LongLat class, used to represent a point on the map. Contains various methods to calculate values relating to
 * locations.
 */
public class LongLat {
    /**
     * the upper longitude boundary of the confinement zone
     */
    static final double UPPER_LONG = -3.184319;
    /**
     * the lower longitude boundary of the confinement zone
     */
    static final double LOWER_LONG = -3.192473;
    /**
     * the upper latitude boundary of the confinement zone
     */
    static final double UPPER_LAT = 55.946233;
    /**
     * the lower latitude boundary of the confinement zone
     */
    static final double LOWER_LAT = 55.942617;

    /**
     * the distance tolerance in degrees, specifically a point is close to another point if the distance between them
     * is strictly less than this value
     */
    static final double DIST_TOL = 0.00015;


    private final double longitude;
    private final double latitude;


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

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    /**
     * Determines whether this LongLat object is within the confinement zone.
     *
     * @return true if the LongLat is within the confinement zone
     */
    private boolean isConfined() {
        return (this.longitude < UPPER_LONG && this.longitude > LOWER_LONG && this.latitude > LOWER_LAT && this.latitude < UPPER_LAT);
    }

    /**
     * Calculates the straight line distance between this LongLat and a second LongLat object.
     *
     * @param longLat2 a second LongLat object we want to calculate the distance to
     * @return the distance between the two LongLats
     */
    private double distanceTo(LongLat longLat2) {
        return Math.sqrt(Math.pow((this.longitude - longLat2.longitude), 2) + Math.pow((this.latitude - longLat2.latitude), 2));
    }

    /**
     * Calculates if this LongLat object is "close to" a second LongLat. We define two points as being close to
     * each other if the distance between them is strictly less than the distance tolerance constant.
     *
     * @param longLat2 a second LongLat object that we want to know if our LongLat object is close to
     * @return true if the two LongLats are close to each other
     */
    public boolean closeTo(LongLat longLat2) {
        return (this.distanceTo(longLat2) < DIST_TOL);
    }

    /**
     * Calculates if a straight line move from this LongLat to a second LongLat would cross into the no fly zone.
     *
     * @param longLat2 the LongLat we want to move to
     * @param noFlyZone the current no fly zone
     * @return true if crosses into the no fly zone
     */
    public boolean crossesNoFlyZone(LongLat longLat2, ArrayList<ArrayList<Line2D>> noFlyZone) {
        Line2D move = new Line2D.Double(this.longitude,this.latitude,longLat2.longitude,longLat2.latitude);
        for (ArrayList<Line2D> polygon : noFlyZone) {
            for (Line2D line : polygon) {
                if (move.intersectsLine(line)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the closest landmark to this LongLat and a second LongLat we want to move to, that does not require
     * entering the no fly zone. Used to find an alternate route when moving directly to the second location would
     * cross into the no fly zone.
     *
     * @param longLat2 the LongLat we want to move to
     * @param noFlyZone the current no fly zone
     * @param landmarks the current list of landmarks
     * @return the LongLat location of the closest landmark
     */
    public LongLat getClosestLandmark(LongLat longLat2, ArrayList<ArrayList<Line2D>> noFlyZone, ArrayList<LongLat> landmarks) {
        LongLat closest = longLat2;
        double smallestDist = 1000000000;
        for (LongLat landmark : landmarks) { //only 2 long, but left in, in case we want to add more landmarks
            double distance = this.distanceTo(landmark) + landmark.distanceTo(longLat2);
            if (distance < smallestDist && !this.crossesNoFlyZone(landmark, noFlyZone) && !landmark.crossesNoFlyZone(longLat2, noFlyZone)) {
                smallestDist = distance;
                closest = landmark;
            }
        }
        if (closest.equals(longLat2)) {
            System.err.println("No landmark reachable");
        }
        return closest;
    }

    /**
     * Calculates the distance of travelling from this LongLat to a second LongLat, including the route it might have to
     * take to avoid the no fly zone.
     *
     * @param longLat2 the LongLat we want to move to
     * @param noFlyZone the current no fly zone
     * @param landmarks the current list of landmarks
     * @return the distance travelled
     */
    public double getTravelDistance(LongLat longLat2, ArrayList<ArrayList<Line2D>> noFlyZone, ArrayList<LongLat> landmarks) {
        double distance = 0;
        if (longLat2.isConfined()) {
            if (this.crossesNoFlyZone(longLat2, noFlyZone)) {
                LongLat closestLandmark = this.getClosestLandmark(longLat2, noFlyZone, landmarks);
                if (closestLandmark.equals(longLat2)) {
                    distance += 1000000000;
                } else {
                    distance += this.distanceTo(closestLandmark) + closestLandmark.distanceTo(longLat2);
                }
            } else {
                distance += this.distanceTo(longLat2);
            }
        }
        else {
            System.err.println("This move is outside the confinement zone");
        }
        return distance;
    }

    /**
     * Calculates the path from this LongLat to a second LongLat, including the landmarks it might have to go to, to
     * avoid entering the no fly zone.
     *
     * @param longLat2 the LongLat we want to move to
     * @param noFlyZone the current no fly zone
     * @param landmarks the current list of landmarks
     * @return the list of LongLats to travel to in a straight line to get to the second LongLat
     */
    public ArrayList<LongLat> getPath(LongLat longLat2, ArrayList<ArrayList<Line2D>> noFlyZone, ArrayList<LongLat> landmarks) {
        if (longLat2.isConfined()) {
            ArrayList<LongLat> path = new ArrayList<>();
            if (this.crossesNoFlyZone(longLat2, noFlyZone)) {
                LongLat closestLandmark = this.getClosestLandmark(longLat2, noFlyZone, landmarks);
                path.add(closestLandmark);
            }
            path.add(longLat2);
            return path;
        }
        else {
            System.err.println("This move is outside the confinement zone");
            ArrayList<LongLat> path = new ArrayList<>();
            path.add(this);
            return path;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongLat longLat = (LongLat) o;
        return Double.compare(longLat.longitude, longitude) == 0 && Double.compare(longLat.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }
}
