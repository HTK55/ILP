package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined() {
        return (this.longitude < -3.184319 && this.longitude > -3.192473 && this.latitude > 55.942617 && this.latitude < 55.946233);
    }

    public double distanceTo(LongLat longLat) {
        return Math.sqrt(Math.pow((this.longitude - longLat.longitude),2) + Math.pow((this.latitude - longLat.latitude),2));
    }

    public boolean closeTo(LongLat longLat) {
        return (this.distanceTo(longLat) < 0.00015);
    }

    public LongLat nextPosition(int angle) {
        if (angle == -999) {
            return this;
        }
        else if (angle >= 0 && angle <= 360 && angle % 10 == 0) {
            double angleInRadians = Math.toRadians(angle);
            double newLongitude = this.longitude + Math.cos(angleInRadians) * 0.00015;
            double newLatitude = this.latitude + Math.sin(angleInRadians) * 0.00015;
            return new LongLat(newLongitude,newLatitude);
        }
        else {
            throw new IllegalArgumentException("This is not a valid angle");
        }
    }
}
