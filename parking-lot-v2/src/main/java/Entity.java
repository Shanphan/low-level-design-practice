import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all entities in the parking lot system.
 * Provides unique identification for each entity.
 * <p>
 * ✅ Made class abstract - you never create Entity directly
 * ✅ Made id field private final - better encapsulation, immutable
 * ✅ Added equals() and hashCode() - needed for collections (HashMap, HashSet)
 * ✅ Added documentation
 */

public abstract class Entity {

    private final UUID id = UUID.randomUUID();

    public Entity() {
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
