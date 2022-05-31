package scot.gov.payment.rest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderCodeValidatorTest {

    OrderCodeValidator sut = new OrderCodeValidator();

    @Test
    public void acceptsOrderCode() {
        assertTrue(sut.isValid("code", null));
    }

    @Test
    public void rejectsNUll() {
        assertFalse(sut.isValid(null, null));
    }

    @Test
    public void rejectsBlank() {
        assertFalse(sut.isValid("", null));
    }

    @Test
    public void rejectsTooLong() {
        assertFalse(sut.isValid("1234567890123456789012345678901234567890123456789012345678901234567890", null));
    }

    @Test
    public void rejectsSpaces() {
        assertFalse(sut.isValid("order code", null));
    }
}
