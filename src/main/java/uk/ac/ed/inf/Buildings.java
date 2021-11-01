package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.awt.Polygon;

public class Buildings {
    // HttpClient for http requests (should be moved to a higher file when implementing Menus http requests in future classes, as should only be one)
    private static final HttpClient client = HttpClient.newHttpClient();

    public final String name;
    public final String port;

    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the webserver is running
     */
    public Buildings(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public void getConfinementZone() {
        String urlString = "http://"+this.name+":"+this.port+"/buildings/no-fly-zones.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FeatureCollection features = FeatureCollection.fromJson(response.body());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
