package model.room;

import model.room.enums.RoomType;

import java.util.Objects;

/**
 * A standard paid hotel room. Implements IRoom.
 * Rooms are uniquely identified by their room number.
 * Two Room objects are considered equal if they share the same room number.
 */
public class Room implements IRoom {

    private final String roomNumber;
    private final Double nightlyRate;
    private final RoomType occupancyType;

    public Room(final String roomNumber, final Double nightlyRate, final RoomType occupancyType) {
        this.roomNumber = roomNumber;
        this.nightlyRate = nightlyRate;
        this.occupancyType = occupancyType;
    }

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public Double getRoomPrice() {
        return nightlyRate;
    }

    @Override
    public RoomType getRoomType() {
        return occupancyType;
    }

    @Override
    public boolean isFree() {
        return nightlyRate != null && nightlyRate == 0.0;
    }

    @Override
    public String toString() {
        String priceDisplay = isFree() ? "Free" : String.format("$%.2f / night", nightlyRate);
        return "Room " + roomNumber + " | " + occupancyType + " | " + priceDisplay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        return Objects.equals(roomNumber, ((Room) o).roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
}
