package uk.ac.ed.inf;


import java.awt.geom.Line2D;
import java.net.http.HttpClient;
import java.sql.Date;
import java.util.ArrayList;


public class App
{
    public static final HttpClient client = HttpClient.newHttpClient();
    public static final LongLat APPLETONTOWER = new LongLat(-3.186874, 55.944494);
    public static final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    public static final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);

    public static void main(String[] args) {
        //Date date = Date.valueOf((args[0]+"-"+args[1]+"-"+args[2]));
        //String websitePort = args[3];
        //String DerbyPort = args[4];
        String date1 = "2023-12-31";
        Derby derbyClient = new Derby("localhost","1527");
        Website websiteClient = new Website("localhost","9898",client);
        ArrayList<Order> orders = derbyClient.getOrdersForDate(Date.valueOf(date1),websiteClient);
        ArrayList<ArrayList<Line2D>> confinementZone = websiteClient.getConfinementZone();
        ArrayList<LongLat> landmarks =  websiteClient.getLandmarks();
        ArrayList<String> shopNames = new ArrayList<>();
        ArrayList<Shop> shops = new ArrayList<>();
        for (Order order : orders) {
            derbyClient.getItemsForOrderNo(order);
            websiteClient.getDeliveryDetails(order);
            for (int i = 0; i < order.getShops().size(); i++) {
                if (!shopNames.contains(order.getShops().get(i))) {
                    shopNames.add(order.getShops().get(i));
                    Shop shop = new Shop(order.getShops().get(i), order.getDeliveredFrom().get(i));
                    shop.addOrder(order, confinementZone, landmarks);
                    shops.add(shop);
                }
            }
        }

        Drone drone = new Drone(APPLETONTOWER, orders, shops);
        while (drone.getMovesLeft() > drone.movesToAppleton(confinementZone, landmarks)) {
            Shop closestShop = drone.getClosestShop(confinementZone, landmarks);
            Order completedOrder = closestShop.getOrders().get(closestShop.getOrders().firstKey());
            drone.moveTo(closestShop.getLocation(), completedOrder, derbyClient);
            ArrayList<LongLat> path = closestShop.getPaths().get(closestShop.getPaths().firstKey());
            for (LongLat location : path) {
                drone.moveTo(location, completedOrder, derbyClient); //add add to flightpath database in derby and implement in move
                drone.nextPosition(-999);
            }
            drone.addCompletedOrder(completedOrder);
            //add add to deliveries database in derby and execute here
            drone.removeOrder(completedOrder);
            for (Shop shop : shops) {
                shop.removeOrder(completedOrder);
            }
        }
        Order home = new Order("Return Home", APPLETONTOWER, "n/a");
        drone.moveTo(APPLETONTOWER, home, derbyClient);

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
