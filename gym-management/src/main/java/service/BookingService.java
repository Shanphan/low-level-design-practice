package service;

import entity.*;
import repository.BookingRepository;
import repository.CustomerRepository;
import repository.GymClassRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final GymClassService gymClassService;
    private final CustomerRepository customerRepository;

    public BookingService(BookingRepository bookingRepository, GymClassService gymClassService, CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.gymClassService = gymClassService;
        this.customerRepository = customerRepository;
    }

    public BookingResponse bookClass(String customerId, String classId, LocalDate date) {
        Customer customer = customerRepository.getById(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + customerId);
        }

        GymClass gymClass = gymClassService.getGymClass(classId);

        if (bookingRepository.isClassAlreadyBookedByUserOnDate(customerId, classId, date)) {
            throw new RuntimeException("Already booked by " + customer.getName());
        }

        int count = bookingRepository.getTotalBookingsForClass(classId, date);
        if (count >= gymClass.getMaxOccupancy()) {
            throw new RuntimeException("Class is full");
        }

        Booking booking = new Booking(customerId, classId, LocalDateTime.now());
        bookingRepository.saveOrUpdate(booking);

        return new BookingResponse(booking.getId(), customer.getName(), gymClass.getClassType().name(),
                gymClass.getStartTime().toString(), gymClass.getEndTime().toString(),
                booking.getBookingTime().toString(), booking.getStatus().name());
    }

    public BookingResponse cancelBooking(String bookingId) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found: " + bookingId);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled: " + bookingId);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.saveOrUpdate(booking);

        GymClass gymClass = gymClassService.getGymClass(booking.getGymClassId());
        Customer customer = customerRepository.getById(booking.getCustomerId());

        return new BookingResponse(booking.getId(), customer.getName(), gymClass.getClassType().name(),
                gymClass.getStartTime().toString(), gymClass.getEndTime().toString(),
                booking.getBookingTime().toString(), booking.getStatus().name());
    }

    public List<Booking> getAllCustomerBookings(String customerId) {
        return bookingRepository.getBookingByCustomerId(customerId);
    }

    public int getBookingsForClass(String classId, LocalDate date) {
        return bookingRepository.getTotalBookingsForClass(classId, date);
    }

    public boolean hasConfirmedBookings(String classId) {
        return bookingRepository.hasConfirmedBookings(classId);
    }
}
