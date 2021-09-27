package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined() {
        return true;
    }

    public double distanceTo(LongLat longLat) {
        return 0;
    }

    public boolean closeTo(LongLat longLat) {
        return true;
    }

    public LongLat nextPosition(int angle) {
        LongLat newLongLat = new LongLat(0.2,0.4);
        return newLongLat;
    }
}
