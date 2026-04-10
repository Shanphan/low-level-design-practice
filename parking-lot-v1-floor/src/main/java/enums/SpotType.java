package enums;

public enum SpotType {
    TWO_WHEELER("Two Wheeler"),
    FOUR_WHEELER("Four Wheeler");

    private final String displayName;

    SpotType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
