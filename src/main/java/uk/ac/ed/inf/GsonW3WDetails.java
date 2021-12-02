package uk.ac.ed.inf;

/**
 * A class used for Gson parsing the information about a what3words location from the webserver. Defines a place to set
 * the coordinates of a json file from on the what3words location.
 */
public class GsonW3WDetails {
    private final GsonCoordinates coordinates;

    public GsonW3WDetails(GsonCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public GsonCoordinates getCoordinates() {
        return coordinates;
    }
}
