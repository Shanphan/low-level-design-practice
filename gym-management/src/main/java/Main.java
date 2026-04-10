import entity.*;
import repository.*;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // --- Wire up repositories ---
        GymRepository gymRepo = new GymRepository();
        GymClassRepository gymClassRepo = new GymClassRepository();
        BookingRepository bookingRepo = new BookingRepository();
        CustomerRepository customerRepo = new CustomerRepository();

        // --- Wire up services ---
        GymClassService gymClassService = new GymClassService(gymClassRepo);
        BookingService bookingService = new BookingService(bookingRepo, gymClassService, customerRepo);
        GymService gymService = new GymService(gymRepo, gymClassRepo, bookingService);
        CustomerService customerService = new CustomerService(customerRepo);

        LocalDate today = LocalDate.now();

        // ============================
        // 1. ADMIN: Add gyms
        // ============================
        System.out.println("=== Adding Gyms ===");
        Gym gym1 = new Gym("Gold's Gym Koramangala", "Koramangala, Bangalore");
        Gym gym2 = new Gym("Gold's Gym Indiranagar", "Indiranagar, Bangalore");
        gymService.addGym(gym1);
        gymService.addGym(gym2);
        System.out.println("Added: " + gym1.getId() + " - " + gym1.getName());
        System.out.println("Added: " + gym2.getId() + " - " + gym2.getName());

        // ============================
        // 2. ADMIN: Add classes
        // ============================
        System.out.println("\n=== Adding Classes ===");
        GymClass yoga = new GymClass(gym1.getId(), ClassType.YOGA, LocalTime.of(7, 0), LocalTime.of(8, 0), 2);
        GymClass zumba = new GymClass(gym1.getId(), ClassType.ZOMBA, LocalTime.of(18, 0), LocalTime.of(19, 0), 3);
        GymClass pilates = new GymClass(gym2.getId(), ClassType.PILATES, LocalTime.of(8, 0), LocalTime.of(9, 0), 1);
        gymClassService.addClass(yoga);
        gymClassService.addClass(zumba);
        gymClassService.addClass(pilates);
        System.out.println("Added: " + yoga.getId() + " - " + yoga.getClassType() + " at " + gym1.getName());
        System.out.println("Added: " + zumba.getId() + " - " + zumba.getClassType() + " at " + gym1.getName());
        System.out.println("Added: " + pilates.getId() + " - " + pilates.getClassType() + " at " + gym2.getName());

        // ============================
        // 3. ADMIN: Try adding class outside 6AM-8PM
        // ============================
        System.out.println("\n=== Edge Case: Class outside 6AM-8PM ===");
        try {
            GymClass lateClass = new GymClass(gym1.getId(), ClassType.YOGA, LocalTime.of(21, 0), LocalTime.of(22, 0), 10);
            gymClassService.addClass(lateClass);
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 4. Add customers
        // ============================
        System.out.println("\n=== Adding Customers ===");
        Customer c1 = new Customer("CUST-1", "Shan");
        Customer c2 = new Customer("CUST-2", "Raj");
        Customer c3 = new Customer("CUST-3", "Priya");
        customerService.addCustomer(c1);
        customerService.addCustomer(c2);
        customerService.addCustomer(c3);
        System.out.println("Added: " + c1.getId() + " - " + c1.getName());
        System.out.println("Added: " + c2.getId() + " - " + c2.getName());
        System.out.println("Added: " + c3.getId() + " - " + c3.getName());

        // ============================
        // 5. CUSTOMER: Book classes
        // ============================
        System.out.println("\n=== Booking Classes ===");
        BookingResponse b1 = bookingService.bookClass(c1.getId(), yoga.getId(), today);
        System.out.println("Booked: " + b1);

        BookingResponse b2 = bookingService.bookClass(c2.getId(), yoga.getId(), today);
        System.out.println("Booked: " + b2);

        // ============================
        // 6. Edge Case: Class full (yoga maxOccupancy = 2)
        // ============================
        System.out.println("\n=== Edge Case: Class Full ===");
        try {
            bookingService.bookClass(c3.getId(), yoga.getId(), today);
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 7. Edge Case: Double booking same class same day
        // ============================
        System.out.println("\n=== Edge Case: Double Booking ===");
        try {
            bookingService.bookClass(c1.getId(), yoga.getId(), today);
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 8. CUSTOMER: Cancel booking
        // ============================
        System.out.println("\n=== Cancel Booking ===");
        BookingResponse cancelled = bookingService.cancelBooking(b2.getId());
        System.out.println("Cancelled: " + cancelled);

        // ============================
        // 9. Edge Case: Cancel already cancelled
        // ============================
        System.out.println("\n=== Edge Case: Cancel Already Cancelled ===");
        try {
            bookingService.cancelBooking(b2.getId());
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 10. Rebook after cancel (freed seat)
        // ============================
        System.out.println("\n=== Rebook After Cancel ===");
        BookingResponse b3 = bookingService.bookClass(c3.getId(), yoga.getId(), today);
        System.out.println("Booked: " + b3);

        // ============================
        // 11. CUSTOMER: View all bookings
        // ============================
        System.out.println("\n=== View Bookings for " + c1.getName() + " ===");
        List<Booking> shanBookings = bookingService.getAllCustomerBookings(c1.getId());
        for (Booking b : shanBookings) {
            System.out.println("  " + b.getId() + " | " + b.getGymClassId() + " | " + b.getStatus());
        }

        // ============================
        // 12. ADMIN: Try removing class with active bookings
        // ============================
        System.out.println("\n=== Edge Case: Remove Class With Active Bookings ===");
        try {
            gymService.removeClass(yoga.getId());
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 13. ADMIN: Try removing gym with active bookings
        // ============================
        System.out.println("\n=== Edge Case: Remove Gym With Active Bookings ===");
        try {
            gymService.removeGym(gym1.getId());
        } catch (RuntimeException e) {
            System.out.println("REJECTED: " + e.getMessage());
        }

        // ============================
        // 14. ADMIN: Remove gym with no bookings (gym2/pilates has no bookings)
        // ============================
        System.out.println("\n=== Remove Gym With No Bookings ===");
        gymService.removeGym(gym2.getId());
        System.out.println("Removed: " + gym2.getId() + " - " + gym2.getName());

        System.out.println("\n=== All scenarios complete ===");
    }
}
