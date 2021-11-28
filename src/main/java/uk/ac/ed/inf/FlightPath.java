package uk.ac.ed.inf;

public class FlightPath {
    private char orderNo;
    private double fromLongitude;
    private double fromLatitude;
    private int angle;
    private double toLongitude;
    private double toLatitude;

    public FlightPath(char orderNo, double fromLongitude, double fromLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
    }
}
