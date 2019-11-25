package scot.gov.payment.service.worldpay.responseurls;

import org.junit.Assert;
import org.junit.Test;
import scot.gov.payment.service.PaymentException;

import java.nio.charset.StandardCharsets;

public class UrlEncoderTest {
    @Test
    public void encodesUrlAsExpected() throws Exception {
        String actual = new UrlEncoder(StandardCharsets.UTF_8.toString()).encodeUrl("http://www.gov,scot/");
        Assert.assertEquals("http%3A%2F%2Fwww.gov%2Cscot%2F", actual);
    }

    @Test(expected = PaymentException.class)
    public void throwsPaymentExceptionIfEncodingNotSupported() throws PaymentException {
        new UrlEncoder("davids special encoding").encodeUrl("http://www.gov,scot/");
    }

}
