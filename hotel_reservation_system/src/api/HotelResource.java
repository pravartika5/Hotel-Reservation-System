package api;

import model.customer.Customer;
import model.reservation.Reservation;
import model.room.IRoom;
import service.customer.CustomerService;
import service.reservation.ReservationService;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * HotelResource is the public API for customer-facing UI operations.
 * It sits between the menu layer and the service layer so neither side
 * depends on the other directly.
 */
public class HotelResource {

    private static final HotelResource INSTANCE = new HotelResource();

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    private HotelResource() {}

    public static HotelResource getInstance() {
        return INSTANCE;
    }

    public void registerNewGuest(final String firstName, final String lastName, final String email) {
        customerService.addGuest(firstName, lastName, email);
    }

    public Customer lookupGuest(final String email) {
        return customerService.findByEmail(email);
    }

    public IRoom lookupRoom(final String roomNumber) {
        return reservationService.fetchRoom(roomNumber);
    }

    public Reservation makeReservation(final String guestEmail, final IRoom room,
                                       final Date arrival, final Date departure) {
        Customer guest = customerService.findByEmail(guestEmail);
        return reservationService.createBooking(guest, room, arrival, departure);
    }

    public Collection<IRoom> searchAvailableRooms(final Date arrival, final Date departure) {
        return reservationService.getAvailableRooms(arrival, departure);
    }

    public Collection<IRoom> searchAlternativeRooms(final Date arrival, final Date departure) {
        return reservationService.getAlternativeAvailableRooms(arrival, departure);
    }

    public Date getAlternativeDate(final Date original) {
        return reservationService.pushDateForward(original);
    }

    public List<Reservation> getGuestReservations(final String guestEmail) {
        Customer guest = customerService.findByEmail(guestEmail);
        if (guest == null) return Collections.emptyList();
        return reservationService.getBookingsByGuest(guest);
    }
}
