package uk.ac.ed.inf;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Website class, used to interface with the web server. Contains methods to obtain and process the information
 * accessed stored on the website.
 */
public class Website {
    /**
     * Standard delivery charge for orders
     */
    static final int STD_CHARGE = 50;
    /**
     * Static client for accessing the website
     */
    static final HttpClient client = HttpClient.newHttpClient();


    public final String name;
    public final String port;
    private ArrayList<GsonShop> shops;

    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the webserver is running
     */
    public Website(String name, String port) {
        this.name = name;
        this.port = port;
    }

    /**
     * Gets all the information about the shops from the website, including processing the items into HashMaps for easy
     * access later.
     */
    public void getShops() {
        String urlString = "http://" + this.name + ":" + this.port + "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<GsonShop> shops = new Gson().fromJson(response.body(), new TypeToken<ArrayList<GsonShop>>() {}.getType());
            for (GsonShop shop : shops) {
                shop.setHashMap();
            }
            this.shops = shops;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the details of the current no fly zone from the website and turns it from geojson polygons into lists of
     * java Line2Ds for easy calculation later.
     *
     * @return the lists of Line2Ds of the no fly zone
     */
    public ArrayList<ArrayList<Line2D>> getNoFlyZone() {
        String urlString = "http://"+this.name+":"+this.port+"/buildings/no-fly-zones.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        ArrayList<ArrayList<Line2D>> noFlyZone = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FeatureCollection features = FeatureCollection.fromJson(response.body());
            assert features.features() != null;
            for (Feature feature : features.features()) {
                ArrayList<Line2D> polygonInLines = new ArrayList<>();
                com.mapbox.geojson.Polygon polygon = (Polygon) feature.geometry();
                assert polygon != null;
                List<List<Point>> listPoints = polygon.coordinates();
                List<Point> points = listPoints.stream().flatMap(Collection::stream).collect(Collectors.toList());
                for (int i = 0; i < (points.size()-1); i++) {
                    Line2D line = new Line2D.Double(points.get(i).longitude(),points.get(i).latitude(),points.get(i+1).longitude(),points.get(i+1).latitude());
                    polygonInLines.add(line);
                }
                noFlyZone.add(polygonInLines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return noFlyZone;
    }

    /**
     * Gets the current list of landmarks from the website.
     *
     * @return a list of LongLat locations of all the landmarks
     */
    public ArrayList<LongLat> getLandmarks() {
        String urlString = "http://"+this.name+":"+this.port+"/buildings/landmarks.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        ArrayList<LongLat> landmarks = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FeatureCollection features = FeatureCollection.fromJson(response.body());
            assert features.features() != null;
            for (Feature feature : features.features()) {
                com.mapbox.geojson.Point point = (Point) feature.geometry();
                assert point != null;
                LongLat longLat = new LongLat(point.longitude(),point.latitude());
                landmarks.add(longLat);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return landmarks;
    }

    /**
     * Gets the shops, and LongLat locations of those shops, an order needs to be picked up from and calculates the cost
     * of the order's delivery.
     *
     * @param order the Order we want the details of
     */
    public void getDeliveryDetails(Order order) {
        int cost = STD_CHARGE;
        ArrayList<LongLat> deliveredFrom = new ArrayList<>();
        HashMap<String, LongLat> shopNames = new HashMap<>();
        for (String item : order.getItemsOrdered()) {
            for (GsonShop shop : this.shops) {
                if (shop.getItems().containsKey(item)) {
                    cost += shop.getItems().get(item);
                    String[] threeWords = shop.getLocation().split("\\.");
                    //Add error
                    String first = threeWords[0];
                    String second = threeWords[1];
                    String third = threeWords[2];
                    LongLat location = getLongLatFromWords(first, second, third);
                    String shopName = shop.getName();

                    if (!deliveredFrom.contains(location)) {
                        deliveredFrom.add(location);
                        shopNames.put(shopName, location);
                    }
                }
            }
        }
        order.setCostInPence(cost);
        order.setDeliveredFrom(deliveredFrom);
        order.setShops(shopNames);
    }

    /**
     * Obtains the LongLat location of a what3words location as stored on the website.
     *
     * @param first first word of w3w location
     * @param second second word of w3w location
     * @param third third word of w3w location
     * @return LongLat location
     */
    public LongLat getLongLatFromWords(String first, String second, String third) {
        String urlString = "http://"+this.name+":"+this.port+"/words/"+first+"/"+second+"/"+third+"/details.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            GsonW3WDetails details = new Gson().fromJson(response.body(), new TypeToken<GsonW3WDetails>(){}.getType());
            return new LongLat(details.getCoordinates().getLng(),details.getCoordinates().getLat());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.err.print("Error in What3Words: no location found");
        return null;
    }

}
