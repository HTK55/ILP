package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class App
{
    public static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        //Date date = Date.valueOf((args[0]+"-"+args[1]+"-"+args[2]));
        //String websitePort = args[3];
        //String DerbyPort = args[4];
        String date1 = "2023-12-31";
        Derby derbyClient = new Derby("localhost","1527");
        Website websiteClient = new Website("localhost","9898",client);
        ArrayList<Order> orders = derbyClient.getOrdersForDate(Date.valueOf(date1),websiteClient);
        ArrayList<String> shopNames = new ArrayList<>();
        ArrayList<Shop> shops = new ArrayList<>();
        for (Order order : orders) {
            derbyClient.getItemsForOrderNo(order);
            websiteClient.getDeliveryDetails(order);
            for (int i = 0; i < order.getShops().size(); i++) {
                if (!shopNames.contains(order.getShops().get(i))) {
                    shopNames.add(order.getShops().get(i));
                    Shop shop = new Shop(order.getShops().get(i), order.getDeliveredFrom().get(i));
                    shop.addOrder(order);
                    shops.add(shop);
                }
            }
        }
        for (Shop shop1 : shops) {
            System.out.println(shop1.getName());
        }
        // Given date x
        // Get orders for date x
        // Map best path
        // Execute
        // Return to Appleton
        // Return completed flightpath database
        // Return completed deliveries database

        System.out.println( "It Runs!" );
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
