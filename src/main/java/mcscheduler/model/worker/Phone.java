package mcscheduler.model.worker;

import static java.util.Objects.requireNonNull;

import mcscheduler.commons.util.AppUtil;

/**
 * Represents a Worker's phone number in the McScheduler.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should only contain numbers and should be a valid Singapore phone number"
            + " (8 digits long, starting with either 6, 8 or 9)\n";
    public static final String VALIDATION_REGEX = "[689]\\d{7}$";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        AppUtil.checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public String toReadableString() {
        return value.substring(0, 4) + " " + value.substring(4);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Phone // instanceof handles nulls
                && value.equals(((Phone) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
