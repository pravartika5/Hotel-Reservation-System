package model.room.enums;

/**
 * RoomType enum represents the type of a hotel room.
 * It can be SINGLE or DOUBLE, each with a corresponding label.
 * Provides a method to get the RoomType by its label.
 * 
 * Example usage:
 *   RoomType single = RoomType.valueOfLabel("1"); // returns SINGLE
 *   RoomType doubleRoom = RoomType.valueOfLabel("2"); // returns DOUBLE
 * 
 * @author pravartika 
 */
public enum RoomType {
    SINGLE("1"),
    DOUBLE("2");

    // Label associated with the room type (e.g., "1" for SINGLE, "2" for DOUBLE)
    public final String label;

    /**
     * Constructor to set the label for the room type.
     * @param label String label for the room type
     */
    private RoomType(String label) {
        this.label = label;
    }

    /**
     * Returns the RoomType corresponding to the given label.
     * @param label String label ("1" or "2")
     * @return RoomType enum value
     * @throws IllegalArgumentException if the label does not match any RoomType
     */
    public static RoomType valueOfLabel(String label) {
        for (RoomType roomType : values()) {
            if (roomType.label.equals(label)) {
                return roomType;
            }
        }
        throw new IllegalArgumentException();
    }
}
