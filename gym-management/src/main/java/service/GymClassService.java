package service;

import entity.GymClass;
import repository.GymClassRepository;

import java.time.LocalTime;
import java.util.List;

public class GymClassService {

    private static final LocalTime EARLIEST_START = LocalTime.of(6, 0);
    private static final LocalTime LATEST_END = LocalTime.of(20, 0);

    private final GymClassRepository gymClassRepository;

    public GymClassService(GymClassRepository gymClassRepository) {
        this.gymClassRepository = gymClassRepository;
    }

    public GymClass addClass(GymClass gymClass) {
        if (gymClass.getStartTime().isBefore(EARLIEST_START)) {
            throw new RuntimeException("Class cannot start before 6 AM");
        }
        if (gymClass.getEndTime().isAfter(LATEST_END)) {
            throw new RuntimeException("Class cannot end after 8 PM");
        }
        if (!gymClass.getStartTime().isBefore(gymClass.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }
        return gymClassRepository.saveOrUpdate(gymClass);
    }

    public GymClass getGymClass(String id) {
        GymClass gymClass = gymClassRepository.getById(id);
        if (gymClass == null) {
            throw new RuntimeException("Class not found: " + id);
        }
        return gymClass;
    }
}
