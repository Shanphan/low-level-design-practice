package repository;

import entity.GymClass;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GymClassRepository {
    private final Map<String, GymClass> gymClasses;

    public GymClassRepository() {
        this.gymClasses = new ConcurrentHashMap<>();
    }

    public GymClass saveOrUpdate(GymClass gymClass) {
        return gymClasses.put(gymClass.getId(), gymClass);
    }

    public void delete(GymClass gymClass) {
        gymClasses.remove(gymClass.getId());

    }

    public GymClass getById(String id) {
        return gymClasses.get(id);
    }

    public List<GymClass> getByGymId(String gymId) {
        return gymClasses.values()
                .stream()
                .filter(gc -> gc.getGymId().equals(gymId))
                .toList();
    }
}
