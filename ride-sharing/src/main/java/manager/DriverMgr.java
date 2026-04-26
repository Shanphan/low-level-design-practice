package manager;

import entity.Driver;
import entity.DriverStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMgr {

    private Map<String, Driver> drivers = new HashMap<>();

    public Driver save(Driver driver) {
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Driver findById(String id) {
        return drivers.get(id);
    }

    public List<Driver> findAvailable() {
        return drivers.values().stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .toList();
    }
}
