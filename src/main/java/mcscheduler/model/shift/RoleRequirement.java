package mcscheduler.model.shift;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import mcscheduler.commons.util.AppUtil;
import mcscheduler.commons.util.CollectionUtil;
import mcscheduler.model.role.Role;

/**
 * Represents a Role Requirement for a shift in the McScheduler.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class RoleRequirement {

    public static final String MESSAGE_CONSTRAINTS = "Role requirements must be of the form 'ROLE QUANTITY_REQUIRED'"
            + " (e.g. 'Cashier 1')\nThe quantity required must be a positive integer.\n";
    public static final String MESSAGE_CONSTRAINTS_STRING_CONSTRUCTOR = "Role requirements must be of the form 'ROLE "
            + "QUANTITY_REQUIRED QUANTITY_FILLED' (e.g. 'Cashier 1 0')\nThe quantity required must be a positive "
            + "integer\nThe quantity filled must be a non-negative integer and at most the quantity required\n";

    public static final String VALIDATION_REGEX = Role.VALIDATION_REGEX + " [1-9]\\d*$";
    public static final String VALIDATION_REGEX_STRING_CONSTRUCTOR = Role.VALIDATION_REGEX + " [1-9]\\d* \\d+$";

    private final Role role;
    private final int quantityRequired;
    private final int quantityFilled;

    /**
     * Creates a role requirement with no workers filling the {@code role} yet.
     * Every field must be present and not null.
     */
    public RoleRequirement(Role role, int quantityRequired) {
        CollectionUtil.requireAllNonNull(role, quantityRequired);
        this.role = role;
        this.quantityRequired = quantityRequired;
        quantityFilled = 0;
    }

    /**
     * Creates a role requirement with the number of workers filling the {@code role}.
     * Every field must be present and not null.
     */
    public RoleRequirement(Role role, int quantityRequired, int quantityFilled) {
        CollectionUtil.requireAllNonNull(role, quantityRequired, quantityFilled);
        assert quantityFilled <= quantityRequired;
        this.role = role;
        this.quantityRequired = quantityRequired;
        this.quantityFilled = quantityFilled;
    }

    /**
     * String version constructor for easy parsing of sample and test data.
     */
    public RoleRequirement(String roleRequirementInfo) {
        requireNonNull(roleRequirementInfo);
        AppUtil.checkArgument(roleRequirementInfo.matches(VALIDATION_REGEX_STRING_CONSTRUCTOR),
                MESSAGE_CONSTRAINTS_STRING_CONSTRUCTOR);

        int lastSpaceIndex = roleRequirementInfo.lastIndexOf(" ");
        quantityFilled = Integer.parseInt(roleRequirementInfo.substring(lastSpaceIndex + 1));
        String lastSpaceRemovedString = roleRequirementInfo.substring(0, lastSpaceIndex);

        int secondLastSpaceIndex = lastSpaceRemovedString.lastIndexOf(" ");
        role = Role.createRole(lastSpaceRemovedString.substring(0, secondLastSpaceIndex));
        quantityRequired = Integer.parseInt(lastSpaceRemovedString.substring(secondLastSpaceIndex + 1));

        AppUtil.checkArgument(quantityFilled <= quantityRequired, MESSAGE_CONSTRAINTS_STRING_CONSTRUCTOR);
    }

    /**
     * Returns true if a given string is a valid role requirement
     */
    public static boolean isValidRoleRequirement(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the quantity filled has not reached the quantity required.
     */
    public boolean isFilled() {
        return quantityFilled >= quantityRequired;
    }

    public Role getRole() {
        return role;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public int getQuantityFilled() {
        return quantityFilled;
    }

    /**
     * Returns true if both role requirements are about the same role and require the same quantity.
     * This defines a weaker notion of equality between the two role requirements.
     */
    public boolean isSameRoleRequirement(RoleRequirement otherRoleRequirement) {
        if (otherRoleRequirement == this) {
            return true;
        }

        return otherRoleRequirement != null
                && otherRoleRequirement.getRole().equals(getRole())
                && otherRoleRequirement.getQuantityRequired() == getQuantityRequired();
    }

    /**
     * Returns true only if both role requirements are about the same role and of the same quantity required.
     * This defines a stronger notion of equality between two role requirements.
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof RoleRequirement
                && role.equals(((RoleRequirement) other).role)
                && quantityRequired == ((RoleRequirement) other).quantityRequired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, quantityRequired);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Role Required: ")
                .append(getRole())
                .append(" x ")
                .append(getQuantityRequired());
        return builder.toString();
    }

}
