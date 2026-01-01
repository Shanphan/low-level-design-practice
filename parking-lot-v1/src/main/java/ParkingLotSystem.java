public class ParkingLotSystem {

    public static void main(String[] args) {


        System.out.println("========================================");
        System.out.println("    PARKING LOT MANAGEMENT SYSTEM      ");
        System.out.println("========================================\n");

        // =============================================
        // STEP 1: Initialize the Parking Lot
        // =============================================
        ParkingSpotManager manager = ParkingSpotManager.getInstance();
        manager.createParkingSpots(5, 5);

        // =============================================
        // STEP 2: Initialize Entry and Exit Points
        // =============================================
        Entrance entrance = Entrance.getInstance(manager);
        Exit exit = Exit.getInstance(manager);

        manager.displayStatus();

        // =============================================
        // STEP 3: Scenario 1 - Park a Two Wheeler
        // =============================================
        try {
            Vehicle bike = new Vehicle("KA01 AB1234", SpotType.TWO_WHEELER);
            Ticket bikeTicket = entrance.parkVehicle(bike);

            // Simulate some time passing (for demo purposes)
            Thread.sleep(1000);  // 1 second = simulates parking duration

            manager.displayStatus();

            // Exit the bike
            double bikeFee = exit.processExit(bikeTicket);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // =============================================
        // STEP 4: Scenario 2 - Park a Four Wheeler
        // =============================================
        try {
            Vehicle car = new Vehicle("MH02 XY5678", SpotType.FOUR_WHEELER);
            Ticket carTicket = entrance.parkVehicle(car);

            manager.displayStatus();

            // Exit the car
            Thread.sleep(500);
            double carFee = exit.processExit(carTicket);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // =============================================
        // STEP 5: Scenario 3 - Multiple Vehicles
        // =============================================
        System.out.println("\n--- Parking Multiple Vehicles ---");

        try {
            Vehicle bike1 = new Vehicle("DL03 CD9012", SpotType.TWO_WHEELER);
            Vehicle bike2 = new Vehicle("TN04 EF3456", SpotType.TWO_WHEELER);
            Vehicle car1 = new Vehicle("KA05 GH7890", SpotType.FOUR_WHEELER);

            Ticket t1 = entrance.parkVehicle(bike1);
            Ticket t2 = entrance.parkVehicle(bike2);
            Ticket t3 = entrance.parkVehicle(car1);

            manager.displayStatus();

            // Exit all
            exit.processExit(t1);
            exit.processExit(t2);
            exit.processExit(t3);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // =============================================
        // STEP 6: Scenario 4 - Parking Lot Full
        // =============================================
        System.out.println("\n--- Testing Parking Lot Full Scenario ---");

        try {
            // Fill all two-wheeler spots
            for (int i = 0; i < 5; i++) {
                Vehicle bike = new Vehicle("BIKE-" + i, SpotType.TWO_WHEELER);
                entrance.parkVehicle(bike);
            }

            manager.displayStatus();

            // Try to park one more - should fail
            Vehicle extraBike = new Vehicle("BIKE-EXTRA", SpotType.TWO_WHEELER);
            entrance.parkVehicle(extraBike);

        } catch (RuntimeException e) {
            System.err.println("Expected error: " + e.getMessage());
        }

        manager.displayStatus();

        // =============================================
        // STEP 7: Dynamic Spot Addition
        // =============================================
        System.out.println("\n--- Adding New Parking Spot ---");
        manager.addParkingSpot(SpotType.TWO_WHEELER);
        manager.displayStatus();

        System.out.println("\n========================================");
        System.out.println("    DEMO COMPLETED SUCCESSFULLY!       ");
        System.out.println("========================================");
    }
}
