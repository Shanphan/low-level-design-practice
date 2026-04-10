package repository;

import entity.Booking;
import entity.BookingStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookingRepository {

    private final Map<String, Booking> bookings;

    public BookingRepository() {
        this.bookings = new ConcurrentHashMap<>();
    }

    public void saveOrUpdate(Booking booking) {
        bookings.put(booking.getId(),  booking);
    }
    public Booking getById(String id) {
        return bookings.get(id);
    }
    public boolean isClassAlreadyBookedByUserOnDate(String userId, String classId, LocalDate date) {
        return bookings.values()
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.CONFIRMED))
                .anyMatch(b -> b.getCustomerId().equals(userId)
                        && b.getGymClassId().equals(classId)
                        && b.getBookingTime().toLocalDate().isEqual(date));
    }
    public int getTotalBookingsForClass(String classId, LocalDate date) {
        return (int) bookings.values()
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.CONFIRMED))
                .filter(b -> b.getGymClassId().equals(classId))
                .filter(b -> b.getBookingTime().toLocalDate().isEqual(date))
                .count();
    }

    public Map<String, Integer> bookingCountPerClassAndDate(LocalDate date) {
         Map<String, Integer> bookingCountPerClass = new HashMap<>();

         for(Map.Entry<String, Booking> entry : bookings.entrySet()) {
             if(entry.getValue().getStatus().equals(BookingStatus.CONFIRMED)
                     && entry.getValue().getBookingTime().toLocalDate().isEqual(date)) {
                 String key = entry.getValue().getGymClassId();
                 bookingCountPerClass.putIfAbsent(key, 0);
                 bookingCountPerClass.put(key, bookingCountPerClass.get(key)+1);
             }
        }

         return bookingCountPerClass;
    }

    public boolean hasConfirmedBookings(String classId) {
        return bookings.values()
                .stream()
                .anyMatch(b -> b.getGymClassId().equals(classId)
                        && b.getStatus() == BookingStatus.CONFIRMED);
    }

    public List<Booking> getBookingByCustomerId(String id) {

        return bookings.values()
                .stream()
                .filter(b -> b.getCustomerId().equals(id))
                .toList();
    }
}