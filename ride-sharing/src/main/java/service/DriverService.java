package service;

import entity.Driver;
import entity.DriverStatus;
import entity.Location;
import exception.DriverNotFoundException;
import manager.DriverMgr;

public class DriverService {

    private final DriverMgr driverMgr;

    public DriverService(DriverMgr driverMgr) {
        this.driverMgr = driverMgr;
    }

    public Driver register(Driver driver) {
        return driverMgr.save(driver);
    }

    public Driver getDriver(String id) {
        Driver driver = driverMgr.findById(id);
        if (driver == null) throw new DriverNotFoundException("Driver not found: " + id);
        return driver;
    }

    public void goOnline(String driverId) {
        Driver driver = getDriver(driverId);
        driver.setStatus(DriverStatus.AVAILABLE);
    }

    public void goOffline(String driverId) {
        Driver driver = getDriver(driverId);
        if (driver.getStatus() == DriverStatus.ON_TRIP) {
            throw new IllegalStateException("Cannot go offline while on a trip");
        }
        driver.setStatus(DriverStatus.OFFLINE);
    }

    public void updateLocation(String driverId, Location location) {
        Driver driver = getDriver(driverId);
        driver.setLocation(location);
    }
}
