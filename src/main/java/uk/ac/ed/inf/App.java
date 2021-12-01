package uk.ac.ed.inf;


import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;


public class App
{
    public static final LongLat APPLETON_TOWER = new LongLat(-3.186874, 55.944494);

    public static void main(String[] args) {
        //Date date = Date.valueOf((args[0]+"-"+args[1]+"-"+args[2]));
        //String websitePort = args[3];
        //String derbyPort = args[4];
        Date date = Date.valueOf("2023-11-30");
        String websitePort = "9898";
        String derbyPort = "1527";
        Derby derbyClient = new Derby("localhost", derbyPort);
        Website websiteClient = new Website("localhost",websitePort);
        ArrayList<Order> orders = derbyClient.getOrdersForDate(date,websiteClient);
        ArrayList<ArrayList<Line2D>> confinementZone = websiteClient.getConfinementZone();
        ArrayList<LongLat> landmarks =  websiteClient.getLandmarks();
        HashMap<String,Shop> shops = new HashMap<>();
        System.out.println(orders.size());
        for (Order order : orders) {
            derbyClient.getItemsForOrderNo(order);
            websiteClient.getDeliveryDetails(order);
            for (String shopName : order.getShops().keySet()) {
                if (!shops.containsKey(shopName)) {
                    Shop shop = new Shop(shopName, order.getShops().get(shopName));
                    shop.addOrder(order, confinementZone, landmarks);
                    shops.put(shopName,shop);
                }
                else {
                    shops.get(shopName).addOrder(order, confinementZone, landmarks);
                }
            }
        }

        for (Order i : orders) {
            System.out.println(i.getW3wLocation());
        }

        Drone drone = new Drone(APPLETON_TOWER, orders, shops);
        while (drone.getMovesLeft() > drone.movesToAppleton(confinementZone, landmarks) + 40) {
            //System.out.println(drone.getMovesLeft());
            //System.out.println(drone.movesToAppleton(confinementZone, landmarks)+'\n');
            Shop bestShop = drone.getBestShop(confinementZone, landmarks);
            if (bestShop.getName().equals("Appleton Tower")) {
                break;
            }
            else {
                Order completedOrder = bestShop.getOrders().firstEntry().getValue(); //closestShop.getOrders().get(closestShop.getOrders().firstKey());
                ArrayList<LongLat> pathToShop = drone.getCurrLoc().getPath(bestShop.getLocation(), confinementZone, landmarks);
                drone.moveTo(pathToShop, completedOrder, derbyClient, landmarks);
                ArrayList<LongLat> path = bestShop.getPaths().firstEntry().getValue(); //closestShop.getPaths().get(closestShop.getPaths().firstKey());
                drone.moveTo(path, completedOrder, derbyClient, landmarks);//add to flightpath
                System.out.println("Completed: "+completedOrder.getOrderNo());
                drone.addCompletedOrder(completedOrder);
                //add add to deliveries database in derby and execute here
                drone.removeOrder(completedOrder);
                drone.removeOrderFromShops(completedOrder);
            }
        }
        System.out.println(drone.getCompletedOrders().size());
        System.out.println(drone.getMovesLeft());
        Order home = new Order("Return Home", APPLETON_TOWER, "n/a");
        ArrayList<LongLat> pathHome = drone.getCurrLoc().getPath(APPLETON_TOWER, confinementZone, landmarks);
        drone.moveTo(pathHome, home, derbyClient, landmarks);
        //System.out.println(drone.getMovesLeft());
        ArrayList<Point> points = new ArrayList<>();
        for (LongLat longLat : drone.getBeenTo()) {
            Point point = Point.fromLngLat(longLat.getLongitude(),longLat.getLatitude());
            points.add(point);
        }
        LineString lineString = LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry((Geometry)lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String json = featureCollection.toJson();

        final Path path = Paths.get("test7.geojson");

        try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            writer.write(json);
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // publish file

        // Move to closest shop with drone.getClosestShop();
        // Get path with shop.getPaths.get(shop.getPaths.FirstKey());
        // Move through path
        // Save to flightpath
        // Remove order from orders and add to completed orders
        // Save to deliveries
        // continue until only enough moves to get back to appleton

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
