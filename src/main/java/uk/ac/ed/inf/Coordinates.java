package uk.ac.ed.inf;

public class Coordinates {
    private final double lng;
    private final double lat;

    public Coordinates(double lng, double lat) {
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
