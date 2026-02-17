package manager;

import entity.Airport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AirportManager {

    private static volatile AirportManager instance;

    private final Map<String, Airport> airportsByCode;
    private final Map<String, List<Airport>> airportsByCity;

    private AirportManager() {
        this.airportsByCode = new HashMap<>();
        this.airportsByCity = new HashMap<>();
    }

    public static AirportManager getInstance() {
        if (instance == null) {
            synchronized (AirportManager.class) {
                if (instance == null) {
                    instance = new AirportManager();
                }
            }
        }
        return instance;
    }

    public void addAirport(Airport airport) {
        airportsByCode.put(airport.getCode(), airport);
        List<Airport> cityAirports = airportsByCity.getOrDefault(airport.getCity(), new ArrayList<>());
        cityAirports.add(airport);
        airportsByCity.put(airport.getCity(), cityAirports);
    }

    public Optional<Airport> getAirportByCode(String code) {
        return Optional.ofNullable(airportsByCode.get(code));
    }

    public List<Airport> getAirportsByCity(String city) {
        return airportsByCity.getOrDefault(city, new ArrayList<>());
    }
}
