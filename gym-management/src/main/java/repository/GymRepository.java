package repository;

import entity.Gym;

import java.util.HashMap;
import java.util.Map;

public class GymRepository {

    private final Map<String, Gym> gyms;

    public GymRepository() {
        this.gyms = new HashMap<>();
    }

    public Gym saveOrUpdate(Gym gym) {
        return gyms.put(gym.getId(), gym);
    }
    public void delete(Gym gym) {
        gyms.remove(gym.getId());

    }
    public Gym getById(String id) {
        return  gyms.get(id);
    }
}
