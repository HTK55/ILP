package uk.ac.ed.inf;

/**
 * A class used for Gson parsing the information about a what3words location from the webserver. Contains the
 * longitude and latitude of a what3words location.
 */
public class GsonCoordinates {
    private final double lng;
    private final double lat;

    public GsonCoordinates(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }
}
