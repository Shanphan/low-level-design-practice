package entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public abstract class Entity {

    private final UUID id = UUID.randomUUID();
}
