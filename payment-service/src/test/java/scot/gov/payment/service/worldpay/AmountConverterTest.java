package scot.gov.payment.service.worldpay;

import org.junit.Test;
import scot.gov.payment.service.PaymentException;

import static org.junit.Assert.assertEquals;

/**
 * Created by z418868 on 05/12/2019.
 */
public class AmountConverterTest {

    AmountConverter sut = new AmountConverter();

    @Test
    public void convertsasExpectd() throws Exception {
        assertEquals("50", sut.convert("0.50"));
        assertEquals("153", sut.convert("1.53"));
        assertEquals("100", sut.convert("1"));
        assertEquals("1", sut.convert("0.01"));
        assertEquals("10", sut.convert("0.10"));
        // note that 0.1 is a tenth of a pound (i.e. 10p) not 1p
        assertEquals("10", sut.convert("0.1"));
        // you can leave of the leading 0, or have redundant ones
        assertEquals("10", sut.convert(".1"));
        assertEquals("10", sut.convert("000.1"));
    }

    @Test(expected = PaymentException.class)
    public void rejectsInvalidValue() throws Exception{
        sut.convert("temp pence");
    }

    @Test(expected = PaymentException.class)
    public void rejectsEmptyValue() throws Exception{
        sut.convert("");
    }

    @Test(expected = PaymentException.class)
    public void rejectsNullValue() throws Exception{
        sut.convert(null);
    }
}
