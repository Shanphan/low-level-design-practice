
/**
 * Enum representing different types of parking spots.
 * Easy to extend - just add new vehicle types here.
 * <p>
 * ✅ Added displayName for better UI/printing
 * ✅ Added basePrice to enum - centralized pricing info
 * ✅ Shows how easy it is to add new types (commented example)
 */

public enum SpotType {
    TWO_WHEELER("Two Wheeler", 50),
    FOUR_WHEELER("Four Wheeler", 100);
    // Future: TRUCK("Truck", 200), ELECTRIC("Electric", 75), etc.

    private final String displayName;
    private final int basePrice;  // Price per hour

    SpotType(String displayName, int basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBasePrice() {
        return basePrice;
    }
}
