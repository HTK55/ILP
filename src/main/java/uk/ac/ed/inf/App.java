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

/**
 * The Main function where the algorithm is executed.
 */
public class App
{
    /**
     * Starting and ending location for the drone, as in the drone's home location (currently Appleton Tower)
     */
    public static final LongLat HOME_LOCATION = new LongLat(-3.186874, 55.944494);
    /**
     * The amount of moves we think we will need to make one more delivery before returning to home
     */
    public static final int MOVE_UNCERTAINTY = 35;
    /**
     * The machine name where we are running the web server and the derby database
     */
    public static final String MACHINE_NAME = "localhost";


    public static void main(String[] args) {
        Date date = Date.valueOf((args[2]+"-"+args[1]+"-"+args[0]));
        String websitePort = args[3];
        String derbyPort = args[4];

        //Setup of the algorithm, collecting data on orders for the supplied day from the database and web server
        Derby derbyClient = new Derby(MACHINE_NAME, derbyPort);
        Website websiteClient = new Website(MACHINE_NAME,websitePort);
        ArrayList<Order> orders = derbyClient.getOrdersForDate(date,websiteClient);
        ArrayList<ArrayList<Line2D>> noFlyZone = websiteClient.getNoFlyZone();
        ArrayList<LongLat> landmarks =  websiteClient.getLandmarks();
        HashMap<String,Shop> shops = new HashMap<>();
        System.out.println("Number of orders for the day: "+orders.size());
        websiteClient.getShops();
        for (Order order : orders) {
            derbyClient.getItemsForOrderNo(order);
            websiteClient.getDeliveryDetails(order);
            for (String shopName : order.getShops().keySet()) {
                if (!shops.containsKey(shopName)) {
                    Shop shop = new Shop(shopName, order.getShops().get(shopName));
                    shop.addOrder(order, noFlyZone, landmarks);
                    shops.put(shopName,shop);
                }
                else {
                    shops.get(shopName).addOrder(order, noFlyZone, landmarks);
                }
            }
        }
        derbyClient.createFlightpath();
        derbyClient.createDeliveries();


        // Executing the algorithm
        Drone drone = new Drone(HOME_LOCATION, orders, shops, derbyClient);
        drone.setNoFlyZone(noFlyZone);
        drone.setLandmarks(landmarks);
        while (drone.getMovesLeft() > drone.movesToHome() + MOVE_UNCERTAINTY) {
            Shop bestShop = drone.getBestShop();
            if (bestShop.getName().equals("Home")) {
                break;
            }
            else {
                Order completedOrder = bestShop.getOrders().firstEntry().getValue();
                ArrayList<LongLat> pathToShop = drone.getCurrLoc().getPath(bestShop.getLocation(), noFlyZone, landmarks);
                drone.moveTo(pathToShop, completedOrder);
                ArrayList<LongLat> path = bestShop.getPaths().firstEntry().getValue();
                drone.moveTo(path, completedOrder);
                drone.addCompletedOrder(completedOrder);
                derbyClient.addToDeliveries(completedOrder);
                drone.removeOrder(completedOrder);
                drone.removeOrderFromShops(completedOrder);
            }
        }


        // Algorithm is finished, returning to Home Location
        System.out.println("Number of orders completed: "+drone.getCompletedOrders().size());
        Order home = new Order("RtrnHome", HOME_LOCATION, "n/a");
        ArrayList<LongLat> pathHome = drone.getCurrLoc().getPath(HOME_LOCATION, noFlyZone, landmarks);
        drone.moveTo(pathHome, home);
        System.out.println("Number of moves left: "+drone.getMovesLeft());


        // Saving the flightpath to a geojson file
        ArrayList<Point> points = new ArrayList<>();
        for (LongLat longLat : drone.getBeenTo()) {
            Point point = Point.fromLngLat(longLat.getLongitude(),longLat.getLatitude());
            points.add(point);
        }
        LineString lineString = LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry(lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String json = featureCollection.toJson();

        final Path path = Paths.get("drone"+args[0]+"-"+args[1]+"-"+args[2]+".geojson");

        try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            writer.write(json);
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( "It Runs!" );
    }
}
