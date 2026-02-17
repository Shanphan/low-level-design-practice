package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Theatre {

    String id;
    String city;
    String address;
    List<Screen> screens;
    List<Show> shows;


    public Theatre(String id, String city, String address) {
        this.id = id;
        this.city = city;
        this.address = address;
        this.screens = new ArrayList<>();
        this.shows = new ArrayList<>();
    }

    public void addScreens(List<Screen> screens) {
        this.screens.addAll(screens);
    }
}
