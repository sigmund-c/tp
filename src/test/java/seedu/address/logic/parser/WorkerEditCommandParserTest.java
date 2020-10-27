package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_ROLE_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PAY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ROLE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NOT_FOUND_ROLE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.PAY_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PAY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_CASHIER;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_CHEF;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PAY_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PAY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_CASHIER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_CHEF;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_WORKER;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_WORKER;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_WORKER;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.WorkerEditCommand;
import seedu.address.logic.commands.WorkerEditCommand.EditWorkerDescriptor;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.shift.Shift;
import seedu.address.model.tag.Role;
import seedu.address.model.worker.Address;
import seedu.address.model.worker.Name;
import seedu.address.model.worker.Pay;
import seedu.address.model.worker.Phone;
import seedu.address.model.worker.Worker;
import seedu.address.testutil.EditWorkerDescriptorBuilder;


public class WorkerEditCommandParserTest {

    private static final String ROLE_EMPTY = " " + PREFIX_ROLE;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, WorkerEditCommand.MESSAGE_USAGE);

    private EditCommandParser parserWithModelStub = new EditCommandParser(new ModelStub());
    private EditCommandParser parserWithModelStubWithRoles = new EditCommandParser(
            new ModelStubWithRoles(Role.createRole(VALID_ROLE_CASHIER), Role.createRole(VALID_ROLE_CHEF)));

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parserWithModelStub, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parserWithModelStub, "1", WorkerEditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parserWithModelStub, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parserWithModelStub, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parserWithModelStub, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parserWithModelStub, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parserWithModelStub, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parserWithModelStub, "1" + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parserWithModelStub, "1" + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parserWithModelStub, "1" + INVALID_PAY_DESC, Pay.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parserWithModelStub, "1" + INVALID_ADDRESS_DESC,
                Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parserWithModelStub, "1" + INVALID_ROLE_DESC, Role.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parserWithModelStub, "1" + INVALID_PHONE_DESC + PAY_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // valid phone followed by invalid phone. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        assertParseFailure(parserWithModelStub, "1" + PHONE_DESC_BOB + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_ROLE} alone will reset the tags of the {@code Worker} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parserWithModelStubWithRoles, "1" + ROLE_DESC_CASHIER + ROLE_DESC_CHEF + ROLE_EMPTY,
                Role.MESSAGE_CONSTRAINTS);
        assertParseFailure(parserWithModelStubWithRoles, "1" + ROLE_DESC_CASHIER + ROLE_EMPTY + ROLE_DESC_CHEF,
                Role.MESSAGE_CONSTRAINTS);
        assertParseFailure(parserWithModelStubWithRoles, "1" + ROLE_EMPTY + ROLE_DESC_CASHIER + ROLE_DESC_CHEF,
                Role.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parserWithModelStub, "1" + INVALID_NAME_DESC + INVALID_PAY_DESC + VALID_ADDRESS_AMY
                + VALID_PHONE_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_roleNotFound_failure() {
        assertParseFailure(parserWithModelStubWithRoles, INDEX_FIRST_WORKER.getOneBased() + NOT_FOUND_ROLE_DESC,
                MESSAGE_ROLE_NOT_FOUND);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_WORKER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + ROLE_DESC_CASHIER
                + PAY_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + ROLE_DESC_CHEF;
        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withPay(VALID_PAY_AMY).withAddress(VALID_ADDRESS_AMY)
                .withRoles(VALID_ROLE_CHEF, VALID_ROLE_CASHIER).build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);

        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_WORKER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + PAY_DESC_AMY;

        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withPay(VALID_PAY_AMY).build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);

        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_WORKER;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withName(VALID_NAME_AMY).build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditWorkerDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);

        // pay
        userInput = targetIndex.getOneBased() + PAY_DESC_AMY;
        descriptor = new EditWorkerDescriptorBuilder().withPay(VALID_PAY_AMY).build();
        expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditWorkerDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);

        // roles
        userInput = targetIndex.getOneBased() + ROLE_DESC_CASHIER;
        descriptor = new EditWorkerDescriptorBuilder().withRoles(VALID_ROLE_CASHIER).build();
        expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_WORKER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + PAY_DESC_AMY
                + ROLE_DESC_CASHIER + PHONE_DESC_AMY + ADDRESS_DESC_AMY + PAY_DESC_AMY + ROLE_DESC_CHEF
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + PAY_DESC_BOB + ROLE_DESC_CASHIER;

        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withPay(VALID_PAY_BOB).withAddress(VALID_ADDRESS_BOB).withRoles(VALID_ROLE_CASHIER, VALID_ROLE_CHEF)
                .build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);

        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_WORKER;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;
        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + PAY_DESC_BOB + INVALID_PHONE_DESC + ADDRESS_DESC_BOB
                + PHONE_DESC_BOB;
        descriptor = new EditWorkerDescriptorBuilder().withPhone(VALID_PHONE_BOB).withPay(VALID_PAY_BOB)
                .withAddress(VALID_ADDRESS_BOB).build();
        expectedCommand = new WorkerEditCommand(targetIndex, descriptor);
        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }

    @Test
    public void parse_resetRoles_success() {
        Index targetIndex = INDEX_THIRD_WORKER;
        String userInput = targetIndex.getOneBased() + ROLE_EMPTY;

        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withRoles().build();
        WorkerEditCommand expectedCommand = new WorkerEditCommand(targetIndex, descriptor);

        assertParseSuccess(parserWithModelStubWithRoles, userInput, expectedCommand);
    }



    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addWorker(Worker worker) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasWorker(Worker worker) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteWorker(Worker target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setWorker(Worker target, Worker editedWorker) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Worker> getFullWorkerList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Worker> getFilteredWorkerList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredWorkerList(Predicate<Worker> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasShift(Shift shift) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteShift(Shift target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addShift(Shift shift) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setShift(Shift target, Shift editedShift) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredShiftList(Predicate<Shift> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Shift> getFullShiftList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Shift> getFilteredShiftList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteAssignment(Assignment target) throws AssignmentNotFoundException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAssignment(Assignment target, Assignment editedAssignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Assignment> getFullAssignmentList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasRole(Role role) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteRole(Role target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addRole(Role role) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setRole(Role target, Role editedRole) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Role> getFilteredRoleList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredRoleList(Predicate<Role> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains two sample roles.
     */
    private class ModelStubWithRoles extends ModelStub {
        private final List<Role> roles;

        ModelStubWithRoles(Role... roles) {
            requireNonNull(roles);
            this.roles = Arrays.asList(roles);
        }

        @Override
        public boolean hasRole(Role role) {
            requireNonNull(role);
            return this.roles.contains(role);
        }

        @Override
        public ObservableList<Role> getFilteredRoleList() {
            return FXCollections.observableList(roles);
        }

        @Override
        public void updateFilteredRoleList(Predicate<Role> predicate) { }
    }
}
