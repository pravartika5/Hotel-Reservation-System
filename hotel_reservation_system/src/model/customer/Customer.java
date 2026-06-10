package model.customer;

import java.util.regex.Pattern;

/**
 * Represents a hotel guest account.
 * Each account has a unique email address (validated on creation),
 * along with a first and last name.
 */
public class Customer {

    // Validates basic email structure: non-empty local part, @, domain, 2-6 letter extension
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[A-Za-z]{2,6}$");

    private final String firstName;
    private final String lastName;
    private final String email;

    /**
     * Builds a Customer after checking the email is properly formatted.
     * @throws IllegalArgumentException if the email format is invalid
     */
    public Customer(final String firstName, final String lastName, final String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "\"" + email + "\" is not a valid email address. "
                            + "Expected format: name@domain.com");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " <" + email + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Customer)) return false;
        Customer other = (Customer) obj;
        return email.equalsIgnoreCase(other.email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}
