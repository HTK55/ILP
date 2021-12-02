package uk.ac.ed.inf;

/**
 * A class used for Gson parsing the information on each shop from the webserver. Contains the name of a shop, the
 * what3words location of the shop and a list of menu items the shop offers.
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
