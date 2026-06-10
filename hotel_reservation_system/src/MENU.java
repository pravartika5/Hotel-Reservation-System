import api.HotelResource;
import model.reservation.Reservation;
import model.room.IRoom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MENU {

    private static final HotelResource hotel = HotelResource.getInstance();
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final Scanner INPUT = new Scanner(System.in);

    public static void mainMenu() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = INPUT.nextLine().trim();
            switch (choice) {
                case "1": handleRoomSearch();          break;
                case "2": handleViewReservations();    break;
                case "3": handleCreateAccount();       break;
                case "4": BookingMenu.adminMenu();     break;
                case "5":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Enter 1-5.");
            }
        }
    }

    static void printMainMenu() {
        System.out.println("\n===== Hotel Reservation System =====");
        System.out.println("1. Find and Reserve a Room");
        System.out.println("2. My Reservations");
        System.out.println("3. Create Account");
        System.out.println("4. Admin");
        System.out.println("5. Exit");
        System.out.print("Select: ");
    }

    // ----------------------------------------------------------------
    // Room search and booking
    // ----------------------------------------------------------------

    private static void handleRoomSearch() {
        // Collect valid dates using loops — no recursion
        Date checkIn = collectDate("Check-in date (MM/dd/yyyy): ");
        if (checkIn == null) return;

        Date checkOut = null;
        while (checkOut == null) {
            checkOut = collectDate("Check-out date (MM/dd/yyyy): ");
            if (checkOut != null && !checkOut.after(checkIn)) {
                System.out.println("Check-out must be after check-in. Try again.");
                checkOut = null;
            }
        }

        // Determine which dates and rooms to present
        Collection<IRoom> roomsToShow;
        Date bookingCheckIn  = checkIn;
        Date bookingCheckOut = checkOut;

        Collection<IRoom> primary = hotel.searchAvailableRooms(checkIn, checkOut);

        if (!primary.isEmpty()) {
            roomsToShow = primary;
        } else {
            Date altIn  = hotel.getAlternativeDate(checkIn);
            Date altOut = hotel.getAlternativeDate(checkOut);
            Collection<IRoom> fallback = hotel.searchAlternativeRooms(checkIn, checkOut);

            if (fallback.isEmpty()) {
                System.out.println("No rooms available on those dates or the next 7 days.");
                return;
            }
            System.out.println("No rooms for your dates. Showing availability for "
                    + altIn + " → " + altOut + " instead.");
            roomsToShow    = fallback;
            bookingCheckIn  = altIn;
            bookingCheckOut = altOut;
        }

        displayRooms(roomsToShow);
        offerBooking(roomsToShow, bookingCheckIn, bookingCheckOut);
    }

    private static void offerBooking(Collection<IRoom> rooms, Date checkIn, Date checkOut) {
        // Loop until valid y/n — no recursion
        String confirm = "";
        while (!confirm.equals("y") && !confirm.equals("n")) {
            System.out.print("Book a room from this list? (y/n): ");
            confirm = INPUT.nextLine().trim().toLowerCase();
        }
        if (confirm.equals("n")) return;

        System.out.print("Your email: ");
        String email = INPUT.nextLine().trim();
        if (hotel.lookupGuest(email) == null) {
            System.out.println("No account found for " + email + ". Please create one first.");
            return;
        }

        // Show rooms again for reference, then ask for number
        displayRooms(rooms);
        System.out.print("Room number to book: ");
        String roomNum = INPUT.nextLine().trim();

        boolean found = false;
        for (IRoom r : rooms) {
            if (r.getRoomNumber().equals(roomNum)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Room " + roomNum + " is not in the available list.");
            return;
        }

        try {
            Reservation r = hotel.makeReservation(email, hotel.lookupRoom(roomNum), checkIn, checkOut);
            System.out.println("\nReservation confirmed:\n" + r);
        } catch (IllegalArgumentException ex) {
            System.out.println("Could not complete booking: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // View reservations
    // ----------------------------------------------------------------

    private static void handleViewReservations() {
        System.out.print("Enter your email: ");
        String email = INPUT.nextLine().trim();
        List<Reservation> list = hotel.getGuestReservations(email);
        if (list.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        int i = 1;
        for (Reservation r : list) {
            System.out.println("\n--- Reservation " + i++ + " ---\n" + r);
        }
    }

    // ----------------------------------------------------------------
    // Account creation
    // ----------------------------------------------------------------

    private static void handleCreateAccount() {
        // Loop until success or user quits — no recursion
        while (true) {
            System.out.print("First name: ");
            String first = INPUT.nextLine().trim();
            System.out.print("Last name: ");
            String last  = INPUT.nextLine().trim();
            System.out.print("Email: ");
            String email = INPUT.nextLine().trim();

            try {
                hotel.registerNewGuest(first, last, email);
                System.out.println("Account created for " + first + ".");
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
                System.out.print("Try again? (y/n): ");
                if (!"y".equals(INPUT.nextLine().trim().toLowerCase())) return;
            }
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    /** Reads and parses a date from stdin. Returns null on bad format. */
    private static Date collectDate(String prompt) {
        System.out.print(prompt);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            return sdf.parse(INPUT.nextLine().trim());
        } catch (ParseException e) {
            System.out.println("Invalid date. Use MM/dd/yyyy, e.g. 06/15/2025.");
            return null;
        }
    }

    private static void displayRooms(Collection<IRoom> rooms) {
        System.out.println("\nAvailable rooms:");
        for (IRoom r : rooms) {
            System.out.println("  " + r);
        }
    }
}
