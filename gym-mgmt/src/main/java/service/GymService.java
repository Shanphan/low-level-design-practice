package service;

import entity.Gym;
import entity.GymClass;
import repository.GymClassRepository;
import repository.GymRepository;

import java.util.List;

public class GymService {

    private final GymRepository gymRepository;
    private final GymClassRepository gymClassRepository;
    private final BookingService bookingService;

    public GymService(GymRepository gymRepository, GymClassRepository gymClassRepository, BookingService bookingService) {
        this.gymRepository = gymRepository;
        this.gymClassRepository = gymClassRepository;
        this.bookingService = bookingService;
    }

    public Gym addGym(Gym gym) {
        return gymRepository.saveOrUpdate(gym);
    }

    public void removeGym(String gymId) {
        Gym gym = gymRepository.getById(gymId);
        if (gym == null) {
            throw new RuntimeException("Gym not found: " + gymId);
        }

        List<GymClass> classes = gymClassRepository.getByGymId(gymId);
        for (GymClass gc : classes) {
            if (bookingService.hasConfirmedBookings(gc.getId())) {
                throw new RuntimeException("Cannot remove gym — class " + gc.getId() + " has active bookings");
            }
        }

        for (GymClass gc : classes) {
            gymClassRepository.delete(gc);
        }
        gymRepository.delete(gym);
    }

    public void removeClass(String classId) {
        GymClass gymClass = gymClassRepository.getById(classId);
        if (gymClass == null) {
            throw new RuntimeException("Class not found: " + classId);
        }

        if (bookingService.hasConfirmedBookings(classId)) {
            throw new RuntimeException("Cannot remove class — has active bookings");
        }

        gymClassRepository.delete(gymClass);
    }
}
