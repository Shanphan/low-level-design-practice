package service;

import entity.*;
import exception.ClassFullException;
import exception.DuplicateBookingException;
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
        gymClass.getLock().lock();
        try {
            List<Booking> bookingsOnDay = bookingRepository.findByUserIdAndClassIdAndDate(customerId, classId, date);
            if (!bookingsOnDay.isEmpty()) {
                throw new DuplicateBookingException("Already booked by " + customer.getName());
            }

            List<Booking> bookings = bookingRepository.findByClassIdAndDate(classId, date);
            if (bookings.size() >= gymClass.getMaxOccupancy()) {
                throw new ClassFullException("Class " + gymClass.getClassType().name() + " is full on " + date);
            }

            Booking booking = new Booking(customerId, classId, LocalDateTime.now());
            bookingRepository.saveOrUpdate(booking);

            return new BookingResponse(booking.getId(), customer.getName(), gymClass.getClassType().name(),
                    gymClass.getStartTime().toString(), gymClass.getEndTime().toString(),
                    booking.getBookingTime().toString(), booking.getStatus().name());
        } finally {
            gymClass.getLock().unlock();
        }
    }

    public BookingResponse cancelBooking(String bookingId) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found: " + bookingId);
        }

        GymClass gymClass = gymClassService.getGymClass(booking.getGymClassId());
        gymClass.getLock().lock();
        try {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                throw new RuntimeException("Booking already cancelled: " + bookingId);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.saveOrUpdate(booking);

            Customer customer = customerRepository.getById(booking.getCustomerId());

            return new BookingResponse(booking.getId(), customer.getName(), gymClass.getClassType().name(),
                    gymClass.getStartTime().toString(), gymClass.getEndTime().toString(),
                    booking.getBookingTime().toString(), booking.getStatus().name());
        } finally {
            gymClass.getLock().unlock();
        }
    }

    public List<Booking> getAllCustomerBookings(String customerId) {
        return bookingRepository.getBookingByCustomerId(customerId);
    }

    public boolean hasConfirmedBookings(String classId) {
        return bookingRepository.hasConfirmedBookings(classId);
    }
}
