package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class App
{
    private static final HttpClient client = HttpClient.newHttpClient();
    public static void main( String[] args )
    {
        new Derby("localhost","1527");
        // Given date
        // Get orders for date
        // Map best path
        // Execute
        // Return to Appleton
        // Return completed flightpath database
        // Return completed deliveries database

        System.out.println( "Hello World!" );
    }


    public class Flightpath {
        private char orderNo;
        private double fromLongitude;
        private double fromLatitude;
        private int angle;
        private double toLongitude;
        private double toLatitude;
    }

    public class Delivery {
        private char orderNo;
        private char deliveredTo;
        private int costInPence;
    }
}
