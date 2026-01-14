package com.design;

public class ElevatorSystemDemo {

    public static void main(String[] args) throws InterruptedException {
        // Create a building with 10 floors and 2 elevators
        Building building = new Building(10, 2);

        System.out.println("========================================");
        System.out.println("SCENARIO 1: External Button Request");
        System.out.println("========================================");

        // Someone on floor 5 wants to go UP
        building.getFloor(5).pressUpButton();
        Thread.sleep(8000);

        System.out.println("\n========================================");
        System.out.println("SCENARIO 2: Internal Button Request");
        System.out.println("========================================");

        // Person inside elevator 0 presses button for floor 8
        building.getInternalButton(0).pressButton(8);
        Thread.sleep(5000);

        System.out.println("\n========================================");
        System.out.println("SCENARIO 3: Multiple Requests");
        System.out.println("========================================");

        // Multiple requests at once
        building.getFloor(2).pressUpButton();
        building.getFloor(7).pressDownButton();
        building.getInternalButton(1).pressButton(9);

        Thread.sleep(15000);

        building.shutdown();
        System.out.println("\nSystem shutdown complete.");
    }

}
