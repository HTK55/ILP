package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Derby {
    public final String name;
    public final String port;
    private Connection conn;
    private Statement statement;

    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the webserver is running
     */
    public Derby(String name, String port) {
        this.name = name;
        this.port = port;
        this.conn = null;
        this.statement = null;
        String jdbcString = "jdbc:derby://"+this.name+":"+this.port+"/derbyDB";
        try {
            this.conn = DriverManager.getConnection(jdbcString);
            this.statement = conn.createStatement();
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
        if (this.conn == null || this.statement == null) {
            System.err.print("Could not connect to the Server, DerbyDB not initialized");
        }
    }

    public Connection getConn() {
        return conn;
    }

    public Statement getStatement() {
        return statement;
    }

    public ArrayList<Order> getOrdersForDate(Date date, Website website) {
        ArrayList<Order> orderList = new ArrayList<>();
        try {
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psOrderQuery = this.getConn().prepareStatement(ordersQuery);
            psOrderQuery.setDate(1, date);
            ResultSet resultSet = psOrderQuery.executeQuery();
            while (resultSet.next()) {
                String w3w = resultSet.getString("deliverTo");
                String[] threeWords = w3w.split("\\.");
                //Add error
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

    public void getItemsForOrderNo(Order order) {
        ArrayList<String> itemList = new ArrayList<>();
        try {
            final String ordersQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psOrderQuery = this.getConn().prepareStatement(ordersQuery);
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

    public void createDeliveries() {
        try {
            DatabaseMetaData databaseMetadata = this.getConn().getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                this.getStatement().execute("drop table deliveries");
            }
            this.getStatement().execute("create table deliveries("+"orderNo char(8), "+"deliveredTo varchar(18), "+"costInPence int)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFlightpath() {
        try {
            DatabaseMetaData databaseMetadata = this.getConn().getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                this.getStatement().execute("drop table flightpath");
            }
            this.getStatement().execute("create table flightpath("+"orderNo char(8), "+"fromLongitude double, "+"costInPence int)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
