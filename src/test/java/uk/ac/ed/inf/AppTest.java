package uk.ac.ed.inf;

import org.junit.Test;

import java.awt.geom.Line2D;
import java.net.http.HttpClient;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AppTest {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);
    public static final HttpClient client = HttpClient.newHttpClient();

    @Test
    public void testIsConfinedTrueA(){
        assertTrue(appletonTower.isConfined());
    }

    @Test
    public void testIsConfinedTrueB(){
        assertTrue(businessSchool.isConfined());
    }

    @Test
    public void testIsConfinedFalse(){
        assertFalse(greyfriarsKirkyard.isConfined());
    }

    private boolean approxEq(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-12;
    }

    @Test
    public void testDistanceTo(){
        double calculatedDistance = 0.0015535481968716011;
        assertTrue(approxEq(appletonTower.distanceTo(businessSchool), calculatedDistance));
    }

    @Test
    public void testCloseToTrue(){
        LongLat alsoAppletonTower = new LongLat(-3.186767933982822, 55.94460006601717);
        assertTrue(appletonTower.closeTo(alsoAppletonTower));
    }


    @Test
    public void testCloseToFalse(){
        assertFalse(appletonTower.closeTo(businessSchool));
    }


    private boolean approxEq(LongLat l1, LongLat l2) {
        return approxEq(l1.longitude, l2.longitude) &&
                approxEq(l1.latitude, l2.latitude);
    }

    @Test
    public void testAngle0(){
        LongLat nextPosition = appletonTower.nextPosition(0);
        LongLat calculatedPosition = new LongLat(-3.186724, 55.944494);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle20(){
        LongLat nextPosition = appletonTower.nextPosition(20);
        LongLat calculatedPosition = new LongLat(-3.186733046106882, 55.9445453030215);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle50(){
        LongLat nextPosition = appletonTower.nextPosition(50);
        LongLat calculatedPosition = new LongLat(-3.186777581858547, 55.94460890666647);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle90(){
        LongLat nextPosition = appletonTower.nextPosition(90);
        LongLat calculatedPosition = new LongLat(-3.186874, 55.944644);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle140(){
        LongLat nextPosition = appletonTower.nextPosition(140);
        LongLat calculatedPosition = new LongLat(-3.1869889066664676, 55.94459041814145);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle190(){
        LongLat nextPosition = appletonTower.nextPosition(190);
        LongLat calculatedPosition = new LongLat(-3.1870217211629517, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle260(){
        LongLat nextPosition = appletonTower.nextPosition(260);
        LongLat calculatedPosition = new LongLat(-3.18690004722665, 55.944346278837045);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle300(){
        LongLat nextPosition = appletonTower.nextPosition(300);
        LongLat calculatedPosition = new LongLat(-3.186799, 55.94436409618943);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle350(){
        LongLat nextPosition = appletonTower.nextPosition(350);
        LongLat calculatedPosition = new LongLat(-3.1867262788370483, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special junk value -999 means "hover and do not change position"
        LongLat nextPosition = appletonTower.nextPosition(-999);
        assertTrue(approxEq(nextPosition, appletonTower));
    }

//    @Test
//    public void testMenus() {
//        // The webserver must be running on port 9898 to run this test.
//        Menus menus = new Menus("localhost", "9898");
//        ArrayList<String> itemsOrdered = new ArrayList<>();
//        itemsOrdered.add("Can of Fanta");
//        itemsOrdered.add("Chicken and avocado wrap");
//        itemsOrdered.add("Hummus, falafel and spicy tomato French country roll");
//        Order order = new Order("1ad5f1ff",itemsOrdered);
//        order = menus.getDelivery(order);
//        int totalCost = order.getCostInPence();
//        assertEquals(230 + 400 + 75 + 50, totalCost);
//        ArrayList<String> shopLocations = new ArrayList<>();
//        shopLocations.add("pest.round.peanut");
//        shopLocations.add("sketch.spill.puzzle");
//        assertEquals(shopLocations,order.getDeliveredFrom());
//    }

//    @Test
//    public void testDerbyGetOrderNosForDate() {
//        Derby derby = new Derby("localhost","1527");
//        Date date = Date.valueOf("2023-12-31");
//        ArrayList<ArrayList<String>> orders = derby.getOrdersForDate(date);
//        ArrayList<String> order = new ArrayList<>();
//        order.add("1ad5f1ff");
//        order.add("spell.stick.scale");
//        assertTrue(orders.contains(order));
//    }
//
//    @Test
//    public void testDerbyGetItemsForOrderNo() {
//        Derby derby = new Derby("localhost","1527");
//        String orderNo = "1ad5f1ff";
//        ArrayList<String> items = derby.getItemsForOrderNo(orderNo);
//        assertTrue(items.contains("Can of Fanta"));
//    }

//    @Test
//    public void testDroneCrossesConfinementZoneFalse() {
//        Website website = new Website("localhost", "9898",client);
//        LongLat droneLoc = new LongLat(-3.186874, 55.944494);
//        ArrayList<Order> orders = new ArrayList<>();
//        Drone drone = new Drone(droneLoc,orders);
//        Line2D move = new Line2D.Double(-3.186874, 55.944494,-3.186103,55.944656);
//        assertFalse(drone.crossesConfineZone(website,move));
//    }
//
//    @Test
//    public void testDroneCrossesConfinementZoneTrue() {
//        Website website = new Website("localhost", "9898",client);
//        LongLat droneLoc = new LongLat(-3.186874, 55.944494);
//        ArrayList<Order> orders = new ArrayList<>();
//        Drone drone = new Drone(droneLoc,orders);
//        Line2D move = new Line2D.Double(-3.186874, 55.944494,-3.191065,55.945626);
//        assertTrue(drone.crossesConfineZone(website,move));
//    }

}
