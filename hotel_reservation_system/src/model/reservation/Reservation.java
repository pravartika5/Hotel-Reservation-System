package model.reservation;

import model.customer.Customer;
import model.room.IRoom;

import java.util.Date;

/**
 * Represents a confirmed room booking for a guest.
 * Stores who booked (guest), what was booked (room),
 * and the stay window (arrival and departure dates).
 */
public class Reservation {

    private final Customer guest;
    private final IRoom room;
    private final Date arrivalDate;
    private final Date departureDate;

    public Reservation(final Customer guest, final IRoom room,
                       final Date arrivalDate, final Date departureDate) {
        this.guest = guest;
        this.room = room;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public Customer getGuest() {
        return guest;
    }

    public IRoom getRoom() {
        return room;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    @Override
    public String toString() {
        return "Guest      : " + guest
                + "\nRoom       : " + room
                + "\nArrival    : " + arrivalDate
                + "\nDeparture  : " + departureDate;
    }
}
