import api.AdminResource;
import model.customer.Customer;
import model.room.FreeRoom;
import model.room.IRoom;
import model.room.Room;
import model.room.enums.RoomType;

import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class BookingMenu {

    private static final AdminResource admin = AdminResource.getInstance();
    private static final Scanner INPUT = new Scanner(System.in);

    static void adminMenu() {
        boolean active = true;
        while (active) {
            printAdminMenu();
            String input = INPUT.nextLine().trim();
            switch (input) {
                case "1": showAllGuests();        break;
                case "2": showAllRooms();         break;
                case "3": showAllReservations();  break;
                case "4": roomWizard();           break;
                case "5": active = false;         break;
                default:
                    System.out.println("Enter 1-5.");
            }
        }
    }

    private static void printAdminMenu() {
        System.out.println("\n===== Admin Panel =====");
        System.out.println("1. All Guests");
        System.out.println("2. All Rooms");
        System.out.println("3. All Reservations");
        System.out.println("4. Add Room");
        System.out.println("5. Main Menu");
        System.out.print("Select: ");
    }

    private static void showAllGuests() {
        Collection<Customer> guests = admin.listAllGuests();
        if (guests.isEmpty()) {
            System.out.println("No guests registered.");
            return;
        }
        System.out.println("\nGuests (" + guests.size() + "):");
        for (Customer g : guests) System.out.println("  " + g);
    }

    private static void showAllRooms() {
        Collection<IRoom> rooms = admin.listAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms in system.");
            return;
        }
        System.out.println("\nRooms (" + rooms.size() + "):");
        for (IRoom r : rooms) System.out.println("  " + r);
    }

    private static void showAllReservations() {
        System.out.println("\nAll Reservations:");
        admin.printAllReservations();
    }

    /**
     * Guides admin through adding one or more rooms.
     * All input retry logic uses loops — no recursion.
     */
    private static void roomWizard() {
        boolean addingRooms = true;
        while (addingRooms) {

            // --- Room number ---
            String number = "";
            while (number.isEmpty()) {
                System.out.print("Room number: ");
                number = INPUT.nextLine().trim();
                if (number.isEmpty()) System.out.println("Room number cannot be blank.");
            }

            // --- Price ---
            double price = -1;
            while (price < 0) {
                System.out.print("Nightly price (0 = complimentary): $");
                try {
                    double parsed = Double.parseDouble(INPUT.nextLine().trim());
                    if (parsed < 0) {
                        System.out.println("Price must be 0 or higher.");
                    } else {
                        price = parsed;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter a number, e.g. 120.00");
                }
            }

            // --- Room type ---
            RoomType type = null;
            while (type == null) {
                System.out.println("Room type: 1=Single  2=Double");
                System.out.print("Enter 1 or 2: ");
                String t = INPUT.nextLine().trim();
                try {
                    type = RoomType.valueOfLabel(t);
                } catch (IllegalArgumentException e) {
                    System.out.println("Enter 1 or 2 only.");
                }
            }

            // Build the correct subtype
            IRoom room = (price == 0.0)
                    ? new FreeRoom(number, type)
                    : new Room(number, price, type);

            admin.addRooms(Collections.singletonList(room));
            System.out.println("Added: " + room);

            // --- Another room? ---
            String again = "";
            while (!again.equals("y") && !again.equals("n")) {
                System.out.print("Add another room? (y/n): ");
                again = INPUT.nextLine().trim().toLowerCase();
            }
            addingRooms = again.equals("y");
        }
    }
}
