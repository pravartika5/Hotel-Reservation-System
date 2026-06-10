package service.customer;

import model.customer.Customer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CustomerService maintains all registered guest accounts for this session.
 *
 * Design note: accounts are stored in a plain ArrayList rather than a Map,
 * because the list is expected to be small and a linear scan is readable and
 * straightforward to reason about. Email uniqueness is enforced explicitly
 * before each insertion.
 */
public class CustomerService {

    private static CustomerService instance;

    /** Every registered guest, in the order they signed up. */
    private final List<Customer> guestRoster = new ArrayList<>();

    private CustomerService() {}

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * Registers a new guest account.
     *
     * @throws IllegalArgumentException if the email is already registered
     *         or if it fails the Customer email format validation
     */
    public void addGuest(final String firstName, final String lastName, final String email) {
        // Check uniqueness before creating the Customer object
        if (findByEmail(email) != null) {
            throw new IllegalArgumentException(
                    "An account with the email address \"" + email + "\" is already registered.");
        }
        // Customer constructor validates email format; propagates IllegalArgumentException
        guestRoster.add(new Customer(firstName, lastName, email));
    }

    /**
     * Returns the guest whose email matches (case-insensitive),
     * or null if no such guest exists.
     */
    public Customer findByEmail(final String email) {
        for (Customer guest : guestRoster) {
            if (guest.getEmail().equalsIgnoreCase(email)) {
                return guest;
            }
        }
        return null;
    }

    /** Returns a snapshot of all registered guests. */
    public Collection<Customer> getAllGuests() {
        return new ArrayList<>(guestRoster);
    }
}
