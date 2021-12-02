package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

/**
 * The Derby class, used to interface with the database server to access or store information. Contains methods
 * to process the information retrieved or store information we have processed.
 */
public class Derby {
    public final String name;
    public final String port;


    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the database is running
     */
    public Derby(String name, String port) {
        this.name = name;
        this.port = port;
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet orders = databaseMetadata.getTables(null, null, "ORDERS", null);
            ResultSet orderDetails = databaseMetadata.getTables(null, null, "ORDERDETAILS", null);
            if (!orders.next() || !orderDetails.next()) {
                System.err.print("Error in database: orders table or orderDetails table does not exist");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the orders from the orders table, which contains an order number and w3w location of a delivery,
     * that the drone will need to complete on a specified date. For each order, creates a new Order which contains the
     * order number, w3w location and LongLat location for the delivery. Returns a list of these orders.
     *
     * @param date the date we want the orders for
     * @param website the website client we are using to access the web server
     * @return a list of orders for the supplied date
     */
    public ArrayList<Order> getOrdersForDate(Date date, Website website) {
        ArrayList<Order> orderList = new ArrayList<>();
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psOrderQuery = conn.prepareStatement(ordersQuery);
            psOrderQuery.setDate(1, date);
            ResultSet resultSet = psOrderQuery.executeQuery();
            while (resultSet.next()) {
                String w3w = resultSet.getString("deliverTo");
                String[] threeWords = w3w.split("\\.");
                String first = threeWords[0];
                String second = threeWords[1];
                String third = threeWords[2];
                LongLat deliverTo = website.getLongLatFromWords(first,second,third);
                Order order = new Order(resultSet.getString("orderNo"),deliverTo,w3w);
                orderList.add(order);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return orderList;
    }

    /**
     * Gets the items for a specified Order from the orderDetails table. Adds these items to a list and sets this list
     * as the items ordered for this Order.
     *
     * @param order Order we want to retrieve the items of
     */
    public void getItemsForOrderNo(Order order) {
        ArrayList<String> itemList = new ArrayList<>();
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            final String ordersQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psOrderQuery = conn.prepareStatement(ordersQuery);
            psOrderQuery.setString(1,order.getOrderNo());
            ResultSet resultSet = psOrderQuery.executeQuery();
            while (resultSet.next()) {
                String item = resultSet.getString("item");
                itemList.add(item);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        order.setItemsOrdered(itemList);
    }

    /**
     * Creates a new table flightpath in the database to record the moves the drone makes, where each entry will contain
     * the order number being delivered in that move, the location where the drone starts, the angle it moves at and the
     * location where it ends.
     */
    public void createFlightpath() {
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute("create table flightpath("+"orderNo char(8), "+"fromLongitude double, "+"fromLatitude double, "+"angle integer,"+"toLongitude double, " +"toLatitude double)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an entry to the flightpath table if it has been created.
     *
     * @param orderNo orderNo of the order the drone is currently delivering, must be 8 characters long
     * @param from LongLat of where the drone begins the move at
     * @param angle angle at which the drone is flying
     * @param to LongLat of where the drone ends after the move
     */
    public void addToFlightPath(String orderNo, LongLat from, int angle, LongLat to) {
        if (orderNo.length() > 8) {
            System.err.println("Order number is longer than 8 characters");
        }
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                PreparedStatement psAddToFlightpath = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
                psAddToFlightpath.setString(1, orderNo);
                psAddToFlightpath.setDouble(2, from.getLongitude());
                psAddToFlightpath.setDouble(3, from.getLatitude());
                psAddToFlightpath.setInt(4, angle);
                psAddToFlightpath.setDouble(5, to.getLongitude());
                psAddToFlightpath.setDouble(6, to.getLatitude());
                psAddToFlightpath.execute();
            }
            else {
                System.err.println("Flightpath table does not exist");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new table deliveries in the database to record which deliveries have been completed by the drone, where
     * each entry contains the order number for the delivery, the w3w location it is delivered to and the cost of the
     * order.
     */
    public void createDeliveries() {
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute("create table deliveries("+"orderNo char(8), "+"deliveredTo varchar(18), "+"costInPence int)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an entry to the deliveries table if it has been created.
     *
     * @param order the order that has been completed
     */
    public void addToDeliveries(Order order) {
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                PreparedStatement psAddToDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
                psAddToDeliveries.setString(1, order.getOrderNo());
                psAddToDeliveries.setString(2, order.getW3wLocation());
                psAddToDeliveries.setInt(3, order.getCostInPence());
                psAddToDeliveries.execute();
            }
            else {
                System.err.println("Deliveries table does not exist");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
