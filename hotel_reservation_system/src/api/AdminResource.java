package api;

import model.customer.Customer;
import model.room.IRoom;
import service.customer.CustomerService;
import service.reservation.ReservationService;

import java.util.Collection;
import java.util.List;

/**
 * AdminResource is the public API for admin-facing UI operations.
 * Provides read access to all guests, rooms, and reservations,
 * plus the ability to add new rooms.
 */
public class AdminResource {

    private static final AdminResource INSTANCE = new AdminResource();

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    private AdminResource() {}

    public static AdminResource getInstance() {
        return INSTANCE;
    }

    public Collection<Customer> listAllGuests() {
        return customerService.getAllGuests();
    }

    public Collection<IRoom> listAllRooms() {
        return reservationService.fetchAllRooms();
    }

    public void printAllReservations() {
        reservationService.printAllBookings();
    }

    public void addRooms(final List<IRoom> newRooms) {
        for (IRoom room : newRooms) {
            reservationService.registerRoom(room);
        }
    }
}
