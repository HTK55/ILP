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

public class Website {
    // HttpClient for http requests (should be moved to a higher file when implementing Menus http requests in future classes, as should only be one)

    // Standard delivery charge for orders
    static final int STD_CHARGE = 50;
    static final HttpClient client = HttpClient.newHttpClient();

    public final String name;
    public final String port;

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

    public ArrayList<ArrayList<Line2D>> getConfinementZone() {
        String urlString = "http://"+this.name+":"+this.port+"/buildings/no-fly-zones.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        ArrayList<ArrayList<Line2D>> confinementZone = new ArrayList<>();
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
                confinementZone.add(polygonInLines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return confinementZone;
    }

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
     * Calculates the delivery cost when given some a set of items to be ordered. This is calculated by adding the
     * standard delivery cost to the price of the items, which we get by accessing the webserver. Fails if the server
     * cannot be accessed.
     *
     * @param order the items wanting to be delivered
     */
    public void getDeliveryDetails(Order order) {
        int cost = STD_CHARGE;
        ArrayList<LongLat> deliveredFrom = new ArrayList<>();
        HashMap<String, LongLat> shopNames = new HashMap<>();
        String urlString = "http://"+this.name+":"+this.port+"/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<GsonShop> shops = new Gson().fromJson(response.body(), new TypeToken<List<GsonShop>>() {}.getType());
            for (String item : order.getItemsOrdered()) {
                for (GsonShop shop : shops) {
                    for (GsonMenuItem menuItem : shop.getMenu()) {
                        if (menuItem.getItem().equals(item)) { //reason for hashmap
                            cost += menuItem.getPence();
                            String[] threeWords = shop.getLocation().split("\\.");
                            //Add error
                            String first = threeWords[0];
                            String second = threeWords[1];
                            String third = threeWords[2];
                            LongLat location = getLongLatFromWords(first, second, third);
                            String shopName = shop.getName();

                            if (!deliveredFrom.contains(location)) {
                                deliveredFrom.add(location);
                                shopNames.put(shopName,location);
                            }
                        }
                    }
                }
            }
            order.setCostInPence(cost);
            order.setDeliveredFrom(deliveredFrom);
            order.setShops(shopNames);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

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
