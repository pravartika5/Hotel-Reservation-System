package model.room;

import model.room.enums.RoomType;

/**
 * Contract that all hotel room types must fulfil.
 * Defines the core properties: room number, nightly price,
 * occupancy type, and whether the room is complimentary.
 */
public interface IRoom {

    /** Returns the unique identifier for this room. */
    String getRoomNumber();

    /** Returns the nightly charge. Returns 0.0 for complimentary rooms. */
    Double getRoomPrice();

    /** Returns whether this is a SINGLE or DOUBLE occupancy room. */
    RoomType getRoomType();

    /** Returns true when there is no charge for this room. */
    boolean isFree();
}
