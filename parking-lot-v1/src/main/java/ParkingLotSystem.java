public class ParkingLotSystem {

    public static void main(String[] args) {

        ParkingSpotManager parkingSpotManager = ParkingSpotManager.getInstance();
        parkingSpotManager.createParkingSpots(5, 5);

        //Vehicle
        Vehicle vehicle = new Vehicle("MP09 1234", SpotType.TWO_WHEELER);

        Entrance entrance = Entrance.getInstance(parkingSpotManager);
        ParkingSpot parkingSpot = entrance.findAndUpdateParkingSpot(vehicle);
        Ticket ticket = entrance.generateTicket(parkingSpot);

        //exit
        Exit exit = Exit.getInstance(parkingSpotManager);
        System.out.println(exit.determinePrice(ticket));
        exit.freeParkingSpace(ticket.getParkingSpot());
        System.out.println(" ");



    }
}
