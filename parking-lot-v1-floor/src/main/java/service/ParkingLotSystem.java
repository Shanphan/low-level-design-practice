package service;

import entity.Floor;
import entity.Ticket;
import entity.Vehicle;
import enums.PricingType;
import enums.SpotSelectionType;
import enums.SpotType;
import manager.ParkingSpotManager;
import strategy.PricingStrategyFactory;
import strategy.SpotSelectionStrategyFactory;

import java.util.Map;

public class ParkingLotSystem {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("----------------------------------------");
        System.out.println("   STRATEGY PATTERN DEMONSTRATION      ");
        System.out.println("----------------------------------------");

        // Pricing rates — strategy owns the rates, not the entity
        Map<SpotType, Integer> perMinuteRates = Map.of(
                SpotType.TWO_WHEELER, 2,
                SpotType.FOUR_WHEELER, 5
        );
        Map<SpotType, Integer> hourlyRates = Map.of(
                SpotType.TWO_WHEELER, 50,
                SpotType.FOUR_WHEELER, 100
        );

        // Initialize system — F1/F2 for two-wheelers, F3/F4 for four-wheelers
        ParkingSpotManager manager = ParkingSpotManager.getInstance();
        manager.addFloor(new Floor("F1", SpotType.TWO_WHEELER, 10));
        manager.addFloor(new Floor("F2", SpotType.TWO_WHEELER, 10));
        manager.addFloor(new Floor("F3", SpotType.FOUR_WHEELER, 10));
        manager.addFloor(new Floor("F4", SpotType.FOUR_WHEELER, 10));

        Entrance entrance = Entrance.getInstance(manager);
        Exit exit = Exit.getInstance(manager);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 1: SPOT SELECTION STRATEGIES");
        System.out.println("=".repeat(60));

        // =============================================
        // Test 1: First Available Strategy (Default)
        // =============================================
        System.out.println("\n### TEST 1: First Available Strategy ###");
        manager.setSpotSelectionStrategy(SpotSelectionStrategyFactory.create(SpotSelectionType.FARTHEST));

        Vehicle v1 = new Vehicle("KA01 A001", SpotType.TWO_WHEELER);
        Vehicle v2 = new Vehicle("KA01 A002", SpotType.TWO_WHEELER);

        Ticket t1 = entrance.parkVehicle(v1);
        Ticket t2 = entrance.parkVehicle(v2);

        System.out.println("✓ Both vehicles got farthest available spots");

        // =============================================
        // Test 2: Nearest Spot Strategy
        // =============================================
        System.out.println("\n### TEST 2: Nearest Spot Strategy ###");
        manager.setSpotSelectionStrategy(SpotSelectionStrategyFactory.create(SpotSelectionType.NEAREST));

        Vehicle v3 = new Vehicle("KA01 A003", SpotType.TWO_WHEELER);
        Ticket t3 = entrance.parkVehicle(v3);

        System.out.println("✓ Vehicle assigned nearest available spot");

        // =============================================
        // Test 3: Farthest Spot Strategy again
        // =============================================
        System.out.println("\n### TEST 3: Farthest Spot Strategy ###");
        manager.setSpotSelectionStrategy(SpotSelectionStrategyFactory.create(SpotSelectionType.NEAREST));

        Vehicle v4 = new Vehicle("KA01 A004", SpotType.TWO_WHEELER);
        Vehicle v5 = new Vehicle("KA01 A005", SpotType.TWO_WHEELER);

        Ticket t4 = entrance.parkVehicle(v4);
        Ticket t5 = entrance.parkVehicle(v5);

        System.out.println("✓ Vehicles randomly distributed");

        manager.displayStatus();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 2: PRICING STRATEGIES");
        System.out.println("=".repeat(60));

        // =============================================
        // Test 4: Per-Minute Pricing (Default)
        // =============================================
        System.out.println("\n### TEST 4: Per-Minute Pricing ###");
        exit.setPricingStrategy(PricingStrategyFactory.create(PricingType.valueOf("PER_MINUTE"), perMinuteRates));

        Thread.sleep(2000);  // Simulate 2 seconds parking
        double price1 = exit.processExit(t1);
        System.out.printf("Price with per-minute: ₹%.2f%n", price1);

        // =============================================
        // Test 5: Hourly Pricing with Daily Cap
        // =============================================
        System.out.println("\n### TEST 5: Hourly Pricing with Daily Cap ###");
        exit.setPricingStrategy(PricingStrategyFactory.create(PricingType.valueOf("HOURLY"), hourlyRates));

        double price2 = exit.processExit(t2);
        System.out.printf("Price with hourly (1 hour minimum): ₹%.2f%n", price2);

        // =============================================
        // Test 6: Flat Rate Pricing
        // =============================================
        System.out.println("\n### TEST 6: Flat Rate Pricing ###");
        exit.setPricingStrategy(PricingStrategyFactory.create(PricingType.valueOf("HOURLY"), hourlyRates));

        double price3 = exit.processExit(t3);
        System.out.printf("Price with flat rate: ₹%.2f%n", price3);

        // Clean up remaining vehicles
        exit.setPricingStrategy(PricingStrategyFactory.create(PricingType.valueOf("PER_MINUTE"), perMinuteRates));
        exit.processExit(t4);
        exit.processExit(t5);

        manager.displayStatus();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("KEY TAKEAWAYS");
        System.out.println("=".repeat(60));
        System.out.println("✓ Changed spot selection algorithm WITHOUT changing Manager code");
        System.out.println("✓ Changed pricing algorithm WITHOUT changing Exit code");
        System.out.println("✓ Can add new strategies WITHOUT modifying existing classes");
        System.out.println("✓ Strategies can be swapped at RUNTIME");
        System.out.println("✓ Each strategy is independent and testable");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("STRATEGY PATTERN BENEFITS");
        System.out.println("=".repeat(60));
        System.out.println("1. Open-Closed Principle: Open for extension, closed for modification");
        System.out.println("2. Single Responsibility: Each strategy does ONE thing well");
        System.out.println("3. Flexibility: Change behavior at runtime");
        System.out.println("4. Testability: Test each strategy independently");
        System.out.println("5. Maintainability: Add new strategies without touching old code");

        System.out.println("\n------------------------------------------");
        System.out.println("   DEMO COMPLETED SUCCESSFULLY!        ");
        System.out.println("-------------------------------------------\n");
    }
}