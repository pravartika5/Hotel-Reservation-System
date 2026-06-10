package service.reservation;

import model.customer.Customer;
import model.reservation.Reservation;
import model.room.IRoom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains the room catalogue and all booking records for the current session.
 * Availability is calculated from a temporary candidate map that shrinks as
 * conflicts are detected. Uses lazy-initialised Singleton pattern.
 */
public class ReservationService {

    private static ReservationService instance;

    /** Room catalogue: number → room. HashMap ensures no two rooms share a number. */
    private final Map<String, IRoom> roomCatalog = new HashMap<>();

    /** Flat list of every booking recorded this session. */
    private final List<Reservation> allBookings = new ArrayList<>();

    private static final int FALLBACK_OFFSET_DAYS = 7;

    private ReservationService() {}

    public static ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }

    /** Adds a room; first registration wins if the number is already taken. */
    public void registerRoom(final IRoom room) {
        roomCatalog.putIfAbsent(room.getRoomNumber(), room);
    }

    public IRoom fetchRoom(final String roomNumber) {
        return roomCatalog.get(roomNumber);
    }

    public Collection<IRoom> fetchAllRooms() {
        return new ArrayList<>(roomCatalog.values());
    }

    /**
     * Records a new booking after verifying no date conflict exists for the room.
     * Throws IllegalArgumentException if the requested window intersects an
     * existing booking on the same room.
     */
    public Reservation createBooking(final Customer guest, final IRoom room,
                                     final Date arrival, final Date departure) {
        for (Reservation existing : allBookings) {
            if (existing.getRoom().equals(room)
                    && reservationsIntersect(
                            arrival, departure,
                            existing.getArrivalDate(), existing.getDepartureDate())) {
                throw new IllegalArgumentException(
                        "Room " + room.getRoomNumber()
                                + " is unavailable from " + arrival
                                + " to " + departure + ".");
            }
        }
        Reservation booking = new Reservation(guest, room, arrival, departure);
        allBookings.add(booking);
        return booking;
    }

    /**
     * Returns true when stay [startA, endA) overlaps with stay [startB, endB).
     *
     * Two stays do NOT overlap when one finishes at or before the other starts
     * (endA <= startB), or one starts at or after the other ends (startA >= endB).
     * Negating both conditions gives the overlap case.
     */
    private boolean reservationsIntersect(final Date startA, final Date endA,
                                          final Date startB, final Date endB) {
        boolean finishesBefore = endA.compareTo(startB) <= 0;
        boolean startsAfter    = startA.compareTo(endB) >= 0;
        return !(finishesBefore || startsAfter);
    }

    /**
     * Builds a candidate map from the full room catalogue, then removes any room
     * whose existing booking conflicts with [arrival, departure). The remainder
     * is the available set.
     */
    public Collection<IRoom> getAvailableRooms(final Date arrival, final Date departure) {
        Map<String, IRoom> candidates = new HashMap<>(roomCatalog);

        for (Reservation booking : allBookings) {
            String num = booking.getRoom().getRoomNumber();
            if (candidates.containsKey(num)
                    && reservationsIntersect(
                            arrival, departure,
                            booking.getArrivalDate(), booking.getDepartureDate())) {
                candidates.remove(num);
            }
        }
        return new ArrayList<>(candidates.values());
    }

    /**
     * Searches for available rooms on dates shifted forward by FALLBACK_OFFSET_DAYS.
     * Called when the originally requested window has no availability.
     */
    public Collection<IRoom> getAlternativeAvailableRooms(final Date arrival, final Date departure) {
        return getAvailableRooms(pushDateForward(arrival), pushDateForward(departure));
    }

    /** Returns a new Date shifted forward by FALLBACK_OFFSET_DAYS. */
    public Date pushDateForward(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, FALLBACK_OFFSET_DAYS);
        return cal.getTime();
    }

    /** Returns all bookings recorded for the given guest. */
    public List<Reservation> getBookingsByGuest(final Customer guest) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation b : allBookings) {
            if (b.getGuest().getEmail().equalsIgnoreCase(guest.getEmail())) {
                result.add(b);
            }
        }
        return result;
    }

    /** Prints all bookings to stdout, numbered sequentially. */
    public void printAllBookings() {
        if (allBookings.isEmpty()) {
            System.out.println("No reservations on record.");
            return;
        }
        int n = 1;
        for (Reservation b : allBookings) {
            System.out.println("\n[" + n++ + "] " + b);
        }
        System.out.println();
    }
}
