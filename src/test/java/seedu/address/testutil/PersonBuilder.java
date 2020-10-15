package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
//import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Pay;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ShiftRoleAssignment;
import seedu.address.model.tag.Role;
//import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_PAY = "12.20";
    //public static final String DEFAULT_EMAIL = "alice@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private Phone phone;
    private Pay pay;
    //private Email email;
    private Address address;
    private Set<Role> roles;
    private Set<ShiftRoleAssignment> shiftRoleAssignments;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        pay = new Pay(DEFAULT_PAY);
        //email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        roles = new HashSet<>();
        shiftRoleAssignments = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        pay = personToCopy.getPay();
        //email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        roles = new HashSet<>(personToCopy.getRoles());
        shiftRoleAssignments = new HashSet<>(personToCopy.getShiftRoleAssignments());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code roles} into a {@code Set<Role>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withRoles(String ... roles) {
        this.roles = SampleDataUtil.getRoleSet(roles);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }


    /**
     * Sets the {@code Pay} of the {@code Person} that we are building.
     */
    public PersonBuilder withPay(String pay) {
        this.pay = new Pay(pay);
        return this;
    }

    /**
     * Parses the {@code shiftRoleAssignmentss} into a {@code Set<ShiftRoleAssignments>} and set it to the
     * {@code Person} that we are building.
     */
    public PersonBuilder withShiftRoleAssignments(String ... shiftRoleAssignments) {
        this.shiftRoleAssignments = SampleDataUtil.getShiftRoleAssignmentSet(shiftRoleAssignments);
        return this;
    }

    /*
    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    /*
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }
     */

    public Person build() {
        return new Person(name, phone, pay, address, roles, shiftRoleAssignments);
    }

}
