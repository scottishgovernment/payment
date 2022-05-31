package scot.gov.payment.rest;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AmountValidatorTest {

    AmountValidator sut = new AmountValidator();

    @Test
    public void rejectsNegativeInt() {
        assertFalse(sut.isValid("-1", null));
    }

    @Test
    public void rejectsNegativeDecimal() {
        assertFalse(sut.isValid("-1.0", null));
    }

    @Test
    public void rejectsNan() {
        assertFalse(sut.isValid("nan", null));
    }

    @Test
    public void rejectsXero() {
        assertFalse(sut.isValid("0", null));
    }
    @Test
    public void acceptsPositiveInt() {
        assertTrue(sut.isValid("1", null));
    }

    @Test
    public void acceptsPositiveDecimal() {
        assertTrue(sut.isValid("1.00", null));
    }
}
