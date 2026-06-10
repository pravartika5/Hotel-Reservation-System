package model.room;

import model.room.enums.RoomType;

/**
 * A complimentary room with no nightly charge.
 * Extends Room and fixes the nightly rate at 0.0.
 * Demonstrates inheritance from Room and polymorphism through IRoom.
 */
public class FreeRoom extends Room {

    public FreeRoom(final String roomNumber, final RoomType occupancyType) {
        super(roomNumber, 0.0, occupancyType);
    }

    @Override
    public String toString() {
        return "Room " + getRoomNumber() + " | " + getRoomType() + " | Complimentary (Free)";
    }
}
