package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Airport {

    String id;
    String name;
    String code; // IATA code (e.g., BLR, DEL, BOM)
    String city;
}
