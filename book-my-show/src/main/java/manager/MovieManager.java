package manager;

import entity.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovieManager {

    private static volatile MovieManager instance;

    Map<String, List<Movie>> moviesByCity;

    private MovieManager() {
        this.moviesByCity = new HashMap<>();
    }

    public static MovieManager getInstance() {
        if (instance == null) {
            synchronized (MovieManager.class) {
                if (instance == null) {
                    instance = new MovieManager();
                }
            }
        }
        return instance;
    }

    public void addMovie(Movie movie, String city) {
        List<Movie> moviesInCity = moviesByCity.getOrDefault(city, new ArrayList<>());
        moviesInCity.add(movie);
        moviesByCity.put(city, moviesInCity);
    }

    public List<Movie> getMoviesByCity(String city) {
        return moviesByCity.getOrDefault(city, new ArrayList<>());
    }

    Optional<Movie> searchByName(String city, String name) {

        List<Movie> moviesInCity = moviesByCity.getOrDefault(city, new ArrayList<>());
        for(Movie movie: moviesInCity) {
            if(movie.getName().equalsIgnoreCase(name)) {
                return Optional.of(movie);
            }
        }
        return Optional.empty();

    }
}
