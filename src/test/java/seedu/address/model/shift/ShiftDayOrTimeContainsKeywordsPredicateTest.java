package seedu.address.model.shift;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalShifts.SHIFT_A;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ShiftBuilder;

public class ShiftDayOrTimeContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> keywords = Arrays.asList("mon");
        List<String> otherKeywords = Arrays.asList("AM", "MON");

        ShiftDayOrTimeContainsKeywordsPredicate predicate = new ShiftDayOrTimeContainsKeywordsPredicate(keywords);
        ShiftDayOrTimeContainsKeywordsPredicate otherPredicate = new ShiftDayOrTimeContainsKeywordsPredicate(
                otherKeywords);

        // null
        assertFalse(predicate.equals(null));

        // same object
        assertTrue(predicate.equals(predicate));

        // different type
        assertFalse(predicate.equals(123));

        // same values -> returns true
        assertTrue(predicate.equals(new ShiftDayOrTimeContainsKeywordsPredicate(keywords)));

        // different values
        assertFalse(predicate.equals(otherPredicate));
    }

    @Test
    public void test_dayOrTimeContainsKeywords_returnsTrue() {
        // One keyword day
        ShiftDayOrTimeContainsKeywordsPredicate predicate =
                new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("MON"));
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("MON").build()));

        // One keyword time
        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("PM"));
        assertTrue(predicate.test(new ShiftBuilder().withShiftTime("PM").build()));

        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("MON", "TUE", "AM"));
        // match multiple keywords
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("MON").withShiftTime("AM").build()));

        // match one keyword
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("TUE").withShiftTime("PM").build()));
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("AM").build()));

        // mixed-case keywords
        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("moN", "tUe", "Am"));
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("MON").withShiftTime("AM").build()));
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("TUE").withShiftTime("PM").build()));
        assertTrue(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("AM").build()));
    }

    @Test
    public void test_dayAndTimeDoesNotContainKeywords_returnsFalse() {
        ShiftDayOrTimeContainsKeywordsPredicate predicate;
        // no keywords
        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(SHIFT_A));

        // non-matching keywords
        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("MON", "TUE", "AM"));
        assertFalse(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("PM").build()));

        // keywords match role requirement but not day nor time
        predicate = new ShiftDayOrTimeContainsKeywordsPredicate(Arrays.asList("cashier", "1"));
        assertFalse(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("PM")
                .withRoleRequirements("cashier 2").build()));
        assertFalse(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("PM")
                .withRoleRequirements("cashier 1").build()));
        assertFalse(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("PM")
                .withRoleRequirements("chef 1").build()));
        assertFalse(predicate.test(new ShiftBuilder().withShiftDay("WED").withShiftTime("PM")
                .withRoleRequirements("cashier 3", "cleaner 1").build()));

    }

}